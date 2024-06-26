from datetime import datetime, timedelta
from lib.api import TrackerAPI
from threading import Lock
import supervision as sv
import numpy as np
import threading
import time
import cv2

class VideoThreadManager:
    def __init__(self, model, CLASS_NAMES_DICT, server):
        self.model = model
        self.CLASS_NAMES_DICT = CLASS_NAMES_DICT
        self.server = server
        self.video_threads = {}
        self.api = TrackerAPI()
        self.stop_signals = {}
        self.zone_counts = {}
        self.line_counters = {}
        self.lock = Lock()
        self.shutdown_flag = False

    def fetch_and_update_threads(self):
        new_settings_list = self.api.get_camera_settings()

        if new_settings_list != None:
            # Filter out settings with duplicate device indices or IP addresses
            filtered_settings = {}
            for setting in new_settings_list:
                key = setting['deviceIndex'] if setting['type'] == 'CONNECTEDCAMERA' else setting['ipAddress']
                if key not in filtered_settings:
                    filtered_settings[key] = setting
                else:
                    # Update the setting if it's more recent
                    existing_setting = filtered_settings[key]
                    if setting['createdDate'] > existing_setting['createdDate']:
                        filtered_settings[key] = setting

            for _, new_setting in filtered_settings.items():
                camera_id = new_setting['id']

                if camera_id not in self.video_threads:
                    self.start_thread(new_setting)
                elif self.video_threads[camera_id]['settings'] != new_setting:
                    self.update_thread(camera_id, new_setting)

            # Stop threads for removed cameras
            current_ids = [setting['id'] for setting in filtered_settings.values()]
            for camera_id in list(self.video_threads.keys()):
                if camera_id not in current_ids:
                    self.stop_thread(camera_id)

    def start_thread(self, camera_settings):
        camera_id = camera_settings['id']
        self.stop_signals[camera_id] = False

        self.line_counters[camera_id] = []

        thread = threading.Thread(target=self.video_processing_thread, args=(camera_settings,))
        thread.daemon = True
        thread.start()
        self.video_threads[camera_id] = {'thread': thread, 'settings': camera_settings}

    def stop_thread(self, camera_id):
        self.stop_signals[camera_id] = True
        self.video_threads[camera_id]['thread'].join()
        
        if camera_id in self.line_counters:
            del self.line_counters[camera_id]

        del self.video_threads[camera_id]

    def update_thread(self, camera_id, new_settings):
        self.stop_thread(camera_id)
        self.start_thread(new_settings)

    def start_count_reporter(self):
        self.reporting_thread = threading.Thread(target=self.report_counts)
        self.reporting_thread.daemon = True
        self.reporting_thread.start()

    def report_counts(self):
        while not self.shutdown_flag:
            # Calculate the time remaining until the start of the next hour
            current_time = datetime.now()
            next_hour = (current_time + timedelta(hours=1)).replace(minute=0, second=0, microsecond=0)
            sleep_duration = (next_hour - current_time).total_seconds()

            time.sleep(sleep_duration)

            with self.lock:
                # Report at the start of the hour
                report_time = datetime.now().isoformat()
                count_reports = []  # Initialize a list to accumulate reports

                for camera_id, targets in self.zone_counts.items():
                    for target_id, zones in targets.items():
                        total_counts = {zone_name: zone_data['total'] for zone_name, zone_data in zones.items()}
                        overall_total = sum(zone_data['total'] for zone_data in zones.values())

                        count_report = {
                            "cameraId": camera_id,
                            "targetId": target_id,
                            "timestamp": report_time,
                            "totalCounts": total_counts,
                            "overallTotal": overall_total
                        }
                        count_reports.append(count_report)

                # Reset zone counts and line counters for the next hour
                for camera_id in self.zone_counts.keys():
                    for line_counter in self.line_counters.get(camera_id, []):
                        line_counter.in_count = 0
                        line_counter.out_count = 0

                self.zone_counts = {}

            # POST the list of reports
            if count_reports:
                self.api.post_count_reports(count_reports)
        
    def get_capture_device(self, camera_settings):
        cap = {}

        if camera_settings["type"] == "CONNECTEDCAMERA":
            deviceIndex = camera_settings["deviceIndex"]
            cap = cv2.VideoCapture(deviceIndex)
        elif camera_settings["type"] == "IPCAMERA":
            cap = cv2.VideoCapture(camera_settings["ipAddress"])
            cap.set(cv2.CAP_PROP_BUFFERSIZE, 2)
        else:
            raise ValueError("Invalid Camera Type.")

        return cap

    def find_rectangle_corners(self, point1: sv.Point, point2: sv.Point):
        # The corners of the rectangle are the two given points and the two calculated points
        corner1 = point1
        corner2 = point2
        corner3 = sv.Point(point1.x, point2.y)
        corner4 = sv.Point(point2.x, point1.y)
        
        return corner1, corner2, corner3, corner4
    
    def calculate_zone_counts(self, zone_type, counters):
        with self.lock:
            in_count_sum = sum(counter.in_count for counter in counters)
            out_count_sum = sum(counter.out_count for counter in counters)

        if zone_type == 0:
            return 0, in_count_sum + out_count_sum
        else:
            return in_count_sum - out_count_sum, out_count_sum
    
    def update_zone_counts(self, camera_id, target_id, zone_name, current, total):
        with self.lock:
            if camera_id not in self.zone_counts:
                self.zone_counts[camera_id] = {}
            if target_id not in self.zone_counts[camera_id]:
                self.zone_counts[camera_id][target_id] = {}
            if zone_name not in self.zone_counts[camera_id][target_id]:
                self.zone_counts[camera_id][target_id][zone_name] = {'current': 0, 'total': 0}

            # Update zone counts
            self.zone_counts[camera_id][target_id][zone_name]['current'] = current
            self.zone_counts[camera_id][target_id][zone_name]['total'] = total

    def video_processing_thread(self, camera_settings):
        camera_id = camera_settings['id']

        # Ensure there's a stop signal entry for this thread
        self.stop_signals[camera_id] = False

        self.line_counters[camera_id] = []

        camera_connected = False
        
        # Class IDs of interest
        CLASS_IDS = [int(key) for key in camera_settings['targets'].keys()]

        counter_sets = []

        for target, target_zones in camera_settings['targets'].items():
            counter_set = {
                'target': int(target),
                'zones': []
            }
            for zone in target_zones:
                zone_start_point = sv.Point(zone['startPoint']['x'], zone['startPoint']['y'])
                zone_end_point = sv.Point(zone['endPoint']['x'], zone['endPoint']['y'])

                if zone['zoneType'] == 'LINE':
                    # Create LineCounter instance to draw a line
                    line_counter = sv.LineZone(start=zone_start_point, end=zone_end_point, triggering_anchors=[sv.Position.CENTER])
                    
                    with self.lock:
                        self.line_counters[camera_id].append(line_counter)

                    counter_set['zones'].append({
                        'name': zone['zoneName'],
                        'type': 0,
                        'counters': [line_counter]
                    })
                
                elif zone['zoneType'] == 'RECTANGLE':
                    corner1, corner2, corner3, corner4 = self.find_rectangle_corners(zone_start_point, zone_end_point)

                    # Create LineCounter instances to draw a rectangle
                    line_counter1 = sv.LineZone(start=corner3, end=corner1, triggering_anchors=[sv.Position.CENTER])
                    line_counter2 = sv.LineZone(start=corner2, end=corner3, triggering_anchors=[sv.Position.CENTER])
                    line_counter3 = sv.LineZone(start=corner4, end=corner2, triggering_anchors=[sv.Position.CENTER])
                    line_counter4 = sv.LineZone(start=corner1, end=corner4, triggering_anchors=[sv.Position.CENTER])

                    line_counters = [line_counter1, line_counter2, line_counter3, line_counter4]
                    
                    with self.lock:
                        self.line_counters[camera_id].extend(line_counters)

                    counter_set['zones'].append({
                        'name': zone['zoneName'],
                        'type': 1,
                        'counters': [line_counter1, line_counter2, line_counter3, line_counter4]
                    })
                
                counter_sets.append(counter_set)

        cap = self.get_capture_device(camera_settings)

        # Create BYTETracker instance
        tracker = sv.ByteTrack()

        # Create instance of annotators
        label_annotator = sv.LabelAnnotator()
        box_annotator = sv.BoundingBoxAnnotator()
        line_annotator = sv.LineZoneAnnotator(thickness=1, text_thickness=1, text_scale=1)

        while not self.stop_signals[camera_id]:
            # Initialize running total counts and current counts
            total_counts = {self.CLASS_NAMES_DICT[class_id]: 0 for class_id in CLASS_IDS}
            current_counts = {self.CLASS_NAMES_DICT[class_id]: 0 for class_id in CLASS_IDS}

            if self.stop_signals[camera_id]:
                print(f"Stopping thread for camera {camera_id}")
                break

            # If the camera is not connected, try to reconnect
            if not camera_connected:
                if cap:
                    cap.release()
    
                cap = self.get_capture_device(camera_settings)

                if cap.isOpened():
                    camera_connected = True
            else:
                ret, frame = cap.read()
                if not ret:
                    print("Camera disconnected")
                    # If the camera is disconnected, set camera_connected to False
                    camera_connected = False
                    continue
                
                # Serve raw frame
                self.server.update_stream(f"raw_frame_{camera_id}", frame, frame_key="raw_frame")

                # Model prediction on single frame and conversion to supervision Detections
                results = next(self.model(frame, stream=True, verbose=False))
                detections = sv.Detections.from_ultralytics(results)
                detections = tracker.update_with_detections(detections)

                # Filtering out detections without trackers
                mask = np.array([tracker_id is not None for tracker_id in detections.tracker_id], dtype=bool)
                detections = detections[mask]
                
                # Init fully annotated frame
                all_annotated_frame = frame.copy()

                for counter_set in counter_sets:
                    target_id = counter_set['target']

                    # Filtering out detections with unwanted classes
                    mask = np.array([class_id == target_id for class_id in detections.class_id], dtype=bool)
                    target_detections = detections[mask]

                    # Format custom labels
                    labels = [
                        f"#{tracker_id} {self.CLASS_NAMES_DICT[class_id]} {confidence:0.2f}"
                        for _, _, confidence, class_id, tracker_id, _
                        in target_detections
                    ]

                    target_current = 0
                    target_total = 0
                    for zone in counter_set['zones']:
                        zone_name = zone['name']
                        zone_type = zone['type']

                        for counter in zone['counters']:
                            counter.trigger(detections=target_detections)

                        current, total = self.calculate_zone_counts(zone_type, zone['counters'])
                        
                        self.update_zone_counts(camera_id, target_id, zone_name, current, total)

                        target_current += current
                        target_total += total

                    current_counts[counter_set["target"]] = target_current
                    total_counts[counter_set["target"]] = target_total

                    # Annotate detection boxes and labels
                    target_frame = box_annotator.annotate(scene=frame.copy(), detections=target_detections)
                    target_frame = label_annotator.annotate(scene=target_frame, detections=target_detections, labels=labels)
                    
                    all_annotated_frame = box_annotator.annotate(scene=all_annotated_frame, detections=target_detections)
                    all_annotated_frame = label_annotator.annotate(scene=all_annotated_frame, detections=target_detections, labels=labels)

                    # Annotate line counters
                    for zone in counter_set['zones']:
                        for counter in zone['counters']:
                            target_frame = line_annotator.annotate(frame=target_frame, line_counter=counter)
                            all_annotated_frame = line_annotator.annotate(frame=all_annotated_frame, line_counter=counter)

                    class_id = counter_set['target']
                    annotated_stream_id = f"frame_{camera_id}_{class_id}"
                    current_count_stream_id = f"current_count_{camera_id}_{class_id}"
                    total_count_stream_id = f"total_count_{camera_id}_{class_id}"

                    self.server.update_stream(annotated_stream_id, target_frame)
                    self.server.update_stream(current_count_stream_id, current_count=current_counts[class_id])
                    self.server.update_stream(total_count_stream_id, total_count=total_counts[class_id])

                self.server.update_stream(f"all_annotated_frame_{camera_id}", all_annotated_frame, frame_key="all_annotated_frame")

        cap.release()
        # Ensure to clear the stop signal for next time
        del self.stop_signals[camera_id]
    
    def shutdown(self):
        self.shutdown_flag = True
        # Stop all video threads
        for camera_id in list(self.video_threads.keys()):
            self.stop_thread(camera_id)
        # Stop the reporting thread
        if hasattr(self, 'reporting_thread'):
            self.reporting_thread.join()    