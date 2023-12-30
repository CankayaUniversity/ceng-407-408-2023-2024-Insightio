import os
import yaml

# Get the directory where the script is located
script_directory = os.path.dirname(os.path.abspath(__file__))

# Construct the path to the default.yaml file
yaml_file_path = os.path.join(script_directory, "default.yaml")

# Load the configuration from the default.yaml file
with open(yaml_file_path, "r") as yaml_file:
    config = yaml.safe_load(yaml_file)

import threading
import torch
import numpy as np
import cv2
import tkinter as tk
from tkinter import *
from ByteTrack.yolox.tracker.byte_tracker import BYTETracker, STrack
from onemetric.cv.utils.iou import box_iou_batch
from dataclasses import dataclass
import supervision as sv
from ultralytics import YOLO
from typing import List
import pymongo
import datetime
import requests
from screeninfo import get_monitors
from tkinter import font
import PIL.Image
import PIL.ImageTk


# Define the BoxZone Class
class BoxZone:
    def __init__(self, top_left, bottom_right):
        self.top_left = top_left
        self.bottom_right = bottom_right
        self.in_count = 0
        self.out_count = 0

    def is_inside(self, detection):
        x_center = (detection[0] + detection[2]) / 2
        y_center = (detection[1] + detection[3]) / 2
        return (self.top_left.x <= x_center <= self.bottom_right.x) and \
               (self.top_left.y <= y_center <= self.bottom_right.y)

    def update_counters(self, detections):
        for detection in detections.xyxy:
            if self.is_inside(detection):
                self.in_count += 1
            else:
                self.out_count += 1


# Connect to API
api_post_url = config["api_post_url"]
api_token = "bee429fb-569f-46d1-bd5d-e999a6ff750c"
sayac = config["sayac"]
api_get_url = config["api_get_url"] + sayac


@dataclass(frozen=True)
class BYTETrackerArgs:
    track_thresh: float = 0.25
    track_buffer: int = 30
    match_thresh: float = 0.8
    aspect_ratio_thresh: float = 3.0
    min_box_area: float = 1.0
    mot20: bool = False


# Converts Detections into format that can be consumed by match_detections_with_tracks function
def detections2boxes(detections: sv.Detections) -> np.ndarray:
    return np.hstack((
        detections.xyxy,
        detections.confidence[:, np.newaxis]
    ))


# Converts List[STrack] into format that can be consumed by match_detections_with_tracks function
def tracks2boxes(tracks: List[STrack]) -> np.ndarray:
    return np.array([
        track.tlbr
        for track
        in tracks
    ], dtype=float)


# Matches our bounding boxes with predictions
def match_detections_with_tracks(
    detections: sv.Detections,
    tracks: List[STrack]
) -> sv.Detections:
    if not np.any(detections.xyxy) or len(tracks) == 0:
        return np.empty((0,))

    tracks_boxes = tracks2boxes(tracks=tracks)
    iou = box_iou_batch(tracks_boxes, detections.xyxy)
    track2detection = np.argmax(iou, axis=1)

    tracker_ids = [None] * len(detections)

    for tracker_index, detection_index in enumerate(track2detection):
        if iou[tracker_index, detection_index] != 0:
            tracker_ids[detection_index] = tracks[tracker_index].track_id

    return tracker_ids


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

# Dict mapping class_id to class_name
CLASS_NAMES_DICT = model.model.names

# Class_ids of interest - bicycle, person
CLASS_ID = [1]

# cap = cv2.VideoCapture(config["rtsp_address"], cv2.CAP_FFMPEG)
cap = cv2.VideoCapture(0)

# Create BYTETracker instance
byte_tracker = BYTETracker(BYTETrackerArgs())

# Create instance of BoxAnnotator and LineCounterAnnotator
box_annotator = sv.BoxAnnotator(color=sv.Color(0, 0, 255), thickness=2, text_thickness=2, text_scale=2)

# Box settings (replace these with actual coordinates from your config)
BOX_TOP_LEFT = sv.Point(100, 100)
BOX_BOTTOM_RIGHT = sv.Point(1150, 650)

# Create BoxZone instance
box_zone = BoxZone(BOX_TOP_LEFT, BOX_BOTTOM_RIGHT)


# Video processing thread
def video_processing_thread():
    camera_connected = False

    while True:
        # If the camera is not connected, try to reconnect
        if not camera_connected:
            cap = cv2.VideoCapture(0)
            if cap.isOpened():
                camera_connected = True
        else:
            # Capture a frame from the webcam
            ret, frame = cap.read()
            if not ret:
                print("Camera disconnected")
                camera_connected = False
                continue

            # Model prediction and detection tracking
            results = model(frame)
            detections = sv.Detections(
                xyxy=results[0].boxes.xyxy.cpu().numpy(),
                confidence=results[0].boxes.conf.cpu().numpy(),
                class_id=results[0].boxes.cls.cpu().numpy().astype(int)
            )
            mask = np.array([class_id in CLASS_ID for class_id in detections.class_id], dtype=bool)
            detections = detections[mask]
            tracks = byte_tracker.update(output_results=detections2boxes(detections=detections), img_info=frame.shape,
                                         img_size=frame.shape)
            tracker_id = match_detections_with_tracks(detections=detections, tracks=tracks)
            detections.tracker_id = np.array(tracker_id)
            mask = np.array([tracker_id is not None for tracker_id in detections.tracker_id], dtype=bool)
            detections = detections[mask]

            # Format custom labels
            labels = [f"#{tracker_id} {CLASS_NAMES_DICT[class_id]} {confidence:0.2f}" for
                      _, _, confidence, class_id, tracker_id in detections]

            # Update BoxZone counters
            box_zone.update_counters(detections)

            # Draw the box on the frame
            cv2.rectangle(frame, (BOX_TOP_LEFT.x, BOX_TOP_LEFT.y), (BOX_BOTTOM_RIGHT.x, BOX_BOTTOM_RIGHT.y),
                          (0, 255, 0), 2)
            cv2.putText(frame, f"In: {box_zone.in_count}", (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 255, 0), 2)
            cv2.putText(frame, f"Out: {box_zone.out_count}", (10, 60), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 255, 0), 2)

            # Display the annotated frame
            cv2.imshow("yolov8", frame)

            if cv2.waitKey(1) == ord('q'):
                break

    cap.release()
    cv2.destroyAllWindows()


if __name__ == "__main__":
    
    video_thread = threading.Thread(target=video_processing_thread)
    video_thread.daemon = True  # The thread will terminate when the main program exits
    video_thread.start()

    video_thread.join()