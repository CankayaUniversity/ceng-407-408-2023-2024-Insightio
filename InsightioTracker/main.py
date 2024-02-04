from lib.server import StreamServer
from lib.api import TrackerAPI
from ultralytics import YOLO
import supervision as sv
import numpy as np
import threading
import torch
import cv2

api = None
server = None

def initialize_model():
    global model, CLASS_NAMES_DICT

    MODEL = "yolov8x-oiv7.pt"
    model = YOLO(MODEL)
    model.fuse()

    if torch.cuda.is_available():
        device = torch.device("cuda")
        print("Using CUDA")
    else:
        device = torch.device("cpu")
        print("Using CPU")
    model.to(device)

    CLASS_NAMES_DICT = model.model.names

def get_capture_device(camera_settings):
    cap = {}

    if camera_settings["Type"] == "ConnectedCamera":
        cap = cv2.VideoCapture(camera_settings["DeviceIndex"])
    
    elif camera_settings["Type"] == "IPCamera":
        cap = cv2.VideoCapture(camera_settings["IpAddress"], cv2.CAP_FFMPEG)

    else:
        raise ValueError("Invalid Camera Type.")

    return cap

def find_rectangle_corners(point1: sv.Point, point2: sv.Point):
    # The corners of the rectangle are the two given points and the two calculated points
    corner1 = point1
    corner2 = point2
    corner3 = sv.Point(point1.x, point2.y)
    corner4 = sv.Point(point2.x, point1.y)
    
    return corner1, corner2, corner3, corner4

# Video processing thread
def video_processing_thread(camera_settings):
    camera_connected = False
    
    # Class_ids of interest - bicycle
    CLASS_IDS = camera_settings["Targets"]

    counter_sets = []

    for zone in camera_settings["Zones"]:
        zone_start_point = sv.Point(zone["StartPoint"]["x"], zone["StartPoint"]["y"])
        zone_end_point = sv.Point(zone["EndPoint"]["x"], zone["EndPoint"]["y"])

        counter_set = {
            "target": zone["Target"],
            "counters": []
        }

        if zone["ZoneType"] == 0:
            # Create LineCounter instance to draw a line
            line_counter = sv.LineZone(start=zone_start_point, end=zone_end_point, triggering_anchors=[sv.Position.CENTER])

            counter_set["counters"] = [line_counter]
        
        elif zone["ZoneType"] == 1:
            corner1, corner2, corner3, corner4 = find_rectangle_corners(zone_start_point, zone_end_point)

            # Create LineCounter instances to draw a rectangle
            line_counter1 = sv.LineZone(start=corner3, end=corner1, triggering_anchors=[sv.Position.CENTER])
            line_counter2 = sv.LineZone(start=corner2, end=corner3, triggering_anchors=[sv.Position.CENTER])
            line_counter3 = sv.LineZone(start=corner4, end=corner2, triggering_anchors=[sv.Position.CENTER])
            line_counter4 = sv.LineZone(start=corner1, end=corner4, triggering_anchors=[sv.Position.CENTER])

            counter_set["counters"] = [line_counter1, line_counter2, line_counter3, line_counter4]
        
        counter_sets.append(counter_set)

    cap = get_capture_device(camera_settings)

    # Create BYTETracker instance
    tracker = sv.ByteTrack()

    # Create instance of annotators
    label_annotator = sv.LabelAnnotator()
    box_annotator = sv.BoundingBoxAnnotator()
    line_annotator = sv.LineZoneAnnotator(thickness=1, text_thickness=1, text_scale=1)

    while True:
        # Initialize running total counts and current counts
        total_counts = {CLASS_NAMES_DICT[class_id]: 0 for class_id in CLASS_IDS}
        current_counts = {CLASS_NAMES_DICT[class_id]: 0 for class_id in CLASS_IDS}

        # If the camera is not connected, try to reconnect
        if not camera_connected:
            cap = get_capture_device(camera_settings)

            if cap.isOpened():
                camera_connected = True
        else:
            # Capture a frame from the webcam
            ret, frame = cap.read()
            if not ret:
                print("Camera disconnected")
                # If the camera is disconnected, set camera_connected to False
                camera_connected = False
                continue

            # Model prediction on single frame and conversion to supervision Detections
            results = model(frame)[0]
            detections = sv.Detections.from_ultralytics(results)
            detections = tracker.update_with_detections(detections)

            # Filtering out detections without trackers
            mask = np.array([tracker_id is not None for tracker_id in detections.tracker_id], dtype=bool)
            detections = detections[mask]
            
            all_annotated_frame = frame.copy()

            for counter_set in counter_sets:
                # Filtering out detections with unwanted classes
                mask = np.array([class_id == counter_set["target"] for class_id in detections.class_id], dtype=bool)
                target_detections = detections[mask]

                # Format custom labels
                labels = [
                    f"#{tracker_id} {CLASS_NAMES_DICT[class_id]} {confidence:0.2f}"
                    for _, _, confidence, class_id, tracker_id, _
                    in target_detections
                ]

                # Updating line counters
                for counter in counter_set["counters"]:
                    in_count, out_count = counter.trigger(detections=target_detections)

                # Get in/out counts
                in_count_sum = 0
                out_count_sum = 0
                for counter in counter_set["counters"]:
                    in_count, out_count = counter.in_count, counter.out_count
                    
                    in_count_sum += in_count
                    out_count_sum += out_count

                if len(counter_set["counters"]) == 1:
                    current_counts[counter_set["target"]] = 0
                    total_counts[counter_set["target"]] = in_count_sum + out_count_sum
                else:
                    current_counts[counter_set["target"]] = in_count_sum - out_count_sum
                    total_counts[counter_set["target"]] = out_count_sum

                # Annotate detection boxes and labels
                target_frame = box_annotator.annotate(scene=frame.copy(), detections=target_detections)
                target_frame = label_annotator.annotate(scene=target_frame, detections=target_detections, labels=labels)
                
                all_annotated_frame = box_annotator.annotate(scene=all_annotated_frame, detections=target_detections)
                all_annotated_frame = label_annotator.annotate(scene=all_annotated_frame, detections=target_detections, labels=labels)

                # Annotate line counters
                for counter in counter_set["counters"]:
                    target_frame = line_annotator.annotate(frame=target_frame, line_counter=counter)
                    all_annotated_frame = line_annotator.annotate(frame=all_annotated_frame, line_counter=counter)

                class_id = counter_set["target"]
                annotated_stream_id = f"frame_{camera_settings['id']}_{class_id}"
                current_count_stream_id = f"current_count_{camera_settings['id']}_{class_id}"
                total_count_stream_id = f"total_count_{camera_settings['id']}_{class_id}"

                server.update_stream(annotated_stream_id, target_frame)
                server.update_stream(current_count_stream_id, current_count=current_counts[class_id])
                server.update_stream(total_count_stream_id, total_count=total_counts[class_id])

            server.update_stream(f"all_annotated_frame_{camera_settings['id']}", all_annotated_frame, frame_key="all_annotated_frame")
            
            if cv2.waitKey(1) == ord('q'):
                break

    cap.release()
    cv2.destroyAllWindows()

if __name__ == "__main__":    
    initialize_model()

    server = StreamServer()
    server.run_server()

    api = TrackerAPI()
    camera_settings_list = api.get_camera_settings()

    video_threads = []
    for camera_settings in camera_settings_list:
        video_thread = threading.Thread(target=video_processing_thread, args=(camera_settings,))
        video_thread.daemon = True
        video_threads.append(video_thread)
        video_thread.start()

    # Join video threads
    for video_thread in video_threads:
        video_thread.join()