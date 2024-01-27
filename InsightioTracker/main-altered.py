from ultralytics import YOLO
import supervision as sv
import streaming_server
import numpy as np
import threading
import torch
import yaml
import cv2
import os

# Get the directory where the script is located
script_directory = os.path.dirname(os.path.abspath(__file__))

# Construct the path to the default.yaml file
yaml_file_path = os.path.join(script_directory, "default.yaml")

# Load the configuration from the default.yaml file
with open(yaml_file_path, "r") as yaml_file:
    config = yaml.safe_load(yaml_file)

def find_rectangle_corners(point1: sv.Point, point2: sv.Point):
    # The corners of the rectangle are the two given points and the two calculated points
    corner1 = point1
    corner2 = point2
    corner3 = sv.Point(point1.x, point2.y)
    corner4 = sv.Point(point2.x, point1.y)
    
    return corner1, corner2, corner3, corner4

MODEL = "yolov8x.pt"
model = YOLO(MODEL)
model.fuse()

if torch.cuda.is_available():
    device = torch.device("cuda")
    print("cuda")
else:
    device = torch.device("cpu")
    print("cpu")
model.to(device)

# Dict maping class_id to class_name
CLASS_NAMES_DICT = model.model.names

# Video processing thread
def video_processing_thread(camera_name):
    camera_connected = False
    
    # Class_ids of interest - bicycle
    CLASS_ID = [42]

    # Line settings
    RECT_START = sv.Point(config["rect_start_x"], config["rect_start_y"])
    RECT_END = sv.Point(config["rect_end_x"], config["rect_end_y"])

    # cap = cv2.VideoCapture(config["rtsp_address"], cv2.CAP_FFMPEG)
    cap = cv2.VideoCapture(0)

    # Create BYTETracker instance
    tracker = sv.ByteTrack()

    corner1, corner2, corner3, corner4 = find_rectangle_corners(RECT_START, RECT_END)

    # Create LineCounter instance
    line_counter1 = sv.LineZone(start=corner3, end=corner1, triggering_anchors=[sv.Position.CENTER])
    line_counter2 = sv.LineZone(start=corner2, end=corner3, triggering_anchors=[sv.Position.CENTER])
    line_counter3 = sv.LineZone(start=corner4, end=corner2, triggering_anchors=[sv.Position.CENTER])
    line_counter4 = sv.LineZone(start=corner1, end=corner4, triggering_anchors=[sv.Position.CENTER])

    line_counters = [line_counter1, line_counter2, line_counter3, line_counter4]

    # Create instance of annotators
    label_annotator = sv.LabelAnnotator()
    box_annotator = sv.BoundingBoxAnnotator()
    line_annotator = sv.LineZoneAnnotator(thickness=1, text_thickness=1, text_scale=1)

    while True:
        # Initialize running total counts and current counts
        total_counts = {CLASS_NAMES_DICT[class_id]: 0 for class_id in CLASS_ID}
        current_counts = {CLASS_NAMES_DICT[class_id]: 0 for class_id in CLASS_ID}

        # If the camera is not connected, try to reconnect
        if not camera_connected:
            # cap = cv2.VideoCapture(config["rtsp_address"], cv2.CAP_FFMPEG)
            cap = cv2.VideoCapture(0)
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

            # Filtering out detections with unwanted classes
            mask = np.array([class_id in CLASS_ID for class_id in detections.class_id], dtype=bool)
            detections = detections[mask]

            # Filtering out detections without trackers
            mask = np.array([tracker_id is not None for tracker_id in detections.tracker_id], dtype=bool)
            detections = detections[mask]

            # Format custom labels
            labels = [
                f"#{tracker_id} {CLASS_NAMES_DICT[class_id]} {confidence:0.2f}"
                for _, _, confidence, class_id, tracker_id, _
                in detections
            ]

            # Updating line counters
            for counter in line_counters:
                in_count, out_count = counter.trigger(detections=detections)

            # Get in/out counts
            in_count_sum = 0
            out_count_sum = 0
            for counter in line_counters:
                in_count, out_count = counter.in_count, counter.out_count
                
                in_count_sum += in_count
                out_count_sum += out_count
            
            current_counts[CLASS_NAMES_DICT[CLASS_ID[0]]] = in_count_sum - out_count_sum
            total_counts[CLASS_NAMES_DICT[CLASS_ID[0]]] = out_count_sum

            # Annotate detection boxes and labels
            annotated_frame = box_annotator.annotate(scene=frame.copy(), detections=detections)
            annotated_frame = label_annotator.annotate(scene=annotated_frame, detections=detections, labels=labels)
            
            # Annotate line counters
            annotated_frame = line_annotator.annotate(frame=annotated_frame, line_counter=line_counter1)
            annotated_frame = line_annotator.annotate(frame=annotated_frame, line_counter=line_counter2)
            annotated_frame = line_annotator.annotate(frame=annotated_frame, line_counter=line_counter3)
            annotated_frame = line_annotator.annotate(frame=annotated_frame, line_counter=line_counter4)

            cv2.imshow("yolov8", annotated_frame)

            # Update the frames and current counts
            streaming_server.update_stream(f"raw_frame_{camera_name}", frame, is_raw=True)

            for class_id in CLASS_ID:
                class_name = CLASS_NAMES_DICT[class_id]
                annotated_stream_id = f"frame_{camera_name}_{class_name}"
                current_count_stream_id = f"current_count_{camera_name}_{class_name}"
                total_count_stream_id = f"total_count_{camera_name}_{class_name}"

                streaming_server.update_stream(annotated_stream_id, annotated_frame)
                streaming_server.update_stream(current_count_stream_id, current_count=current_counts[class_name])
                streaming_server.update_stream(total_count_stream_id, total_count=total_counts[class_name])

            if cv2.waitKey(1) == ord('q'):
                break

    cap.release()
    cv2.destroyAllWindows()

if __name__ == "__main__":
    streaming_server.run_server()

    camera_name = "Camera1"  # Replace with actual camera_name
    video_thread = threading.Thread(target=video_processing_thread, args=(camera_name,))
    video_thread.daemon = True  # The thread will terminate when the main program exits
    video_thread.start()

    video_thread.join()