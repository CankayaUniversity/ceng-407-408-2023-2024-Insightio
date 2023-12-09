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


# Connect to MongoDB
mongo_url = config["mongo_url"]
mongo_client = pymongo.MongoClient(mongo_url)
mongo_db = mongo_client["smart_ankara"]
mongo_collection_counter = mongo_db["bike_counter"]
mongo_collection_hrecords = mongo_db["daily_record"]

# Connect to API
api_post_url = config["api_post_url"]
api_token = "bee429fb-569f-46d1-bd5d-e999a6ff750c"
sayac = config["sayac"]
api_get_url = config["api_get_url"] + sayac

# Check connection
def check_connection():
    try:
        response = requests.get(api_get_url)
        if response.status_code == 200:
            return True
    except:
        return False

# Get data from API
def get_data():
    try:
        response = requests.get(api_get_url)
        if response.status_code == 200:
            data = response.json()
            return data["totalCount"]
        else:
            return 0
    except:
        return 0
    
# Send data to API
def post_data(data):
    try:
        response = requests.post(api_post_url, json=data)
        if response.status_code == 200:
            return True
        else:
            return False
    except:
        return False
    
# Get last daily record from database
def get_last_daily_record():
    try:
        if datetime.datetime.now().strftime("%d") == mongo_collection_hrecords.find().sort([("_id", -1)]).limit(1)[0]["timeField"]:
            last_record = mongo_collection_hrecords.find().sort([("_id", -1)]).limit(1)[0]["count"] 
        else:
            # Clear hourly records
            mongo_collection_hrecords.delete_many({})
            last_record = 0
        
        return last_record
    except:
        return 0
    
# Save hourly record to database
def save_daily_record():
    try:
        # update daily count with new total_count
        mongo_collection_hrecords.update_one({"timeField": datetime.datetime.now().strftime("%d")}, {"$set": {"count": total_count, "timeField": datetime.datetime.now().strftime("%d")}}, upsert=True)    
    except:
        pass

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

# Dict maping class_id to class_name
CLASS_NAMES_DICT = model.model.names

# Class_ids of interest - bicycle, person
CLASS_ID = [1]

# Line settings
LINE_START = sv.Point(config["line_start_x"], config["line_start_y"])
LINE_END = sv.Point(config["line_end_x"], config["line_end_y"])

# Road line settings
ROAD_LINE_1_START = (config["road_line_1_start_x"], config["road_line_1_start_y"])
ROAD_LINE_1_END = (config["road_line_1_end_x"], config["road_line_1_end_y"])

ROAD_LINE_2_START = (config["road_line_2_start_x"], config["road_line_2_start_y"])
ROAD_LINE_2_END = (config["road_line_2_end_x"], config["road_line_2_end_y"])

ROAD_LINE_COLOR = (0, 255, 0)
ROAD_LINE_THICKNESS = config["road_line_thickness"]

# cap = cv2.VideoCapture(config["rtsp_address"], cv2.CAP_FFMPEG)
cap = cv2.VideoCapture(0)

# Create BYTETracker instance
byte_tracker = BYTETracker(BYTETrackerArgs())

# Create LineCounter instance
line_counter = sv.LineZone(start=LINE_START, end=LINE_END)

# Create instance of BoxAnnotator and LineCounterAnnotator
box_annotator = sv.BoxAnnotator(color=sv.Color(255, 0, 0), thickness=4, text_thickness=4, text_scale=2)
line_annotator = sv.LineZoneAnnotator(thickness=4, text_thickness=4, text_scale=2)

# Global variables for counting
total_count = get_last_daily_record()
total_count_lock = threading.Lock()

total_year = get_data()
total_year_lock = threading.Lock()

def update_totals(total):
    global total_count, total_count_lock, total_year, total_year_lock

    with total_count_lock:
        total_count += total
    
    with total_year_lock:
        total_year += total

old_total = 0
show_camera_window = config["show_camera_window"]


def db_thread(data):
    if check_connection():
        if mongo_collection_counter.count_documents({}) > 0:
            for document in mongo_collection_counter.find():
                data["data"].append(document)
            mongo_collection_counter.delete_many({})
        post_data(data)
    else:
        mongo_collection_counter.insert_one(data)

# Video processing thread
def video_processing_thread():
    global old_total, show_camera_window, title, count_label, total_count
    camera_connected = False
    
    while True:
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
            results = model(frame)
            detections = sv.Detections(
                xyxy=results[0].boxes.xyxy.cpu().numpy(),
                confidence=results[0].boxes.conf.cpu().numpy(),
                class_id=results[0].boxes.cls.cpu().numpy().astype(int)
            )

            # Filtering out detections with unwanted classes
            mask = np.array([class_id in CLASS_ID for class_id in detections.class_id], dtype=bool)
            detections = detections[mask]

            # Tracking detections
            tracks = byte_tracker.update(
                output_results=detections2boxes(detections=detections),
                img_info=frame.shape,
                img_size=frame.shape
            )
            tracker_id = match_detections_with_tracks(detections=detections, tracks=tracks)
            detections.tracker_id = np.array(tracker_id)

            # Filtering out detections without trackers
            mask = np.array([tracker_id is not None for tracker_id in detections.tracker_id], dtype=bool)
            detections = detections[mask]


            # Format custom labels
            labels = [
                f"#{tracker_id} {CLASS_NAMES_DICT[class_id]} {confidence:0.2f}"
                for _, _, confidence, class_id, tracker_id
                in detections
            ]

            # Updating line counter
            in_count, out_count = line_counter.trigger(detections=detections)

            # Annotate and display frame
            box_annotated_frame = box_annotator.annotate(scene=frame.copy(), detections=detections, labels=labels)
            all_annotated_frame = line_annotator.annotate(frame=box_annotated_frame, line_counter=line_counter)
            total = in_count + out_count

            if total > old_total:
                update_totals(total - old_total)

                # Save hourly record to database
                save_daily_record()
                
                data = {
                            "counterToken": api_token,
                            "data":[
                                {
                                    "counterName": sayac,
                                    "vehicleCount": total - old_total,
                                    "countDate": datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
                                }
                            ]
                        }
                
                old_total = total
    
                # Send data to API
                request_thread = threading.Thread(target=db_thread)
                request_thread.start()

            if datetime.datetime.now().strftime("%H") == "00":
                total_count = 0
                mongo_collection_hrecords.delete_many({})

            if show_camera_window:
                cv2.imshow("yolov8", all_annotated_frame)

            if cv2.waitKey(1) == ord('q'):
                break

    cap.release()
    cv2.destroyAllWindows()

script_dir = os.path.dirname(os.path.abspath(__file__))

# GUI
def create_counter_gui(root, x, y):
    global show_camera_window, total_count, total_count_lock, total_year, total_year_lock
    counter_window = tk.Toplevel()
    counter_window.geometry(f"1920x1080+{x}+{y}")

    def on_key_press(event):
        if event.keysym == "q":
            root.quit()

    counter_window.bind("<Key>", on_key_press)
    counter_window.title("Counter")
    counter_window.attributes('-fullscreen', True)
    counter_window.state('normal')
    counter_window.wm_attributes('-alpha','0')
    counter_window.wm_attributes('-topmost', True)
    
    # Fonts
    URW_Geometric_Bold = font.Font(family="URW Geometric", name="Geometric-Bold", weight="bold", size=120)
    URW_Geometric_Count_Daily = font.Font(family="URW Geometric", weight="bold", size=200)
    URW_Geometric_Count_Yearly = font.Font(family="URW Geometric", weight="bold", size=200)
    URW_Geometric = font.Font(family="URW Geometric", name="Geometric", weight="normal", size=55)

    # getting screen's height in pixels
    height = counter_window.winfo_screenheight()

    # getting screen's width in pixels
    width = counter_window.winfo_screenwidth()

    # create a canvas with a transparent background
    canvas = tk.Canvas(counter_window, width=width, height=height, highlightthickness=0, background="white")
    canvas.pack(fill="both", expand=True)

    # 1st row
    row1 = tk.PanedWindow(canvas, background="white")
    row1.pack(fill="both", expand=True)
    title = tk.Label(row1, text="BİSİKLET SAYACI", font=URW_Geometric_Bold, justify="center", fg="#2776A3", bg="white", height=2)
    title.grid(row=0, column=0, pady=(25, 0), padx=(0, 0))

    line_frame = tk.Frame(row1, width=width, height=1, bg="#D1E0EA")
    line_frame.grid(row=1, column=0, columnspan=3, pady=(0, 25), padx=(0, 0))

    label_width = min(int((width / 3) - 500), 475)

    # 2nd row
    paned_window = tk.PanedWindow(canvas, orient=tk.HORIZONTAL, sashrelief="flat", sashwidth=0, borderwidth=0)
    paned_window.pack(fill=tk.BOTH, expand=True, padx=(250, 200), pady=(0, 0))

    # Create a frame with a colored background
    frame = tk.Frame(paned_window, bg="white", width=label_width)
    frame.pack(fill="both", expand=True)

    # Load your icon image
    icon1 = PIL.ImageTk.PhotoImage(PIL.Image.open(os.path.join(script_dir, 'assets', 'Günlük_Frame.png')))

    # Create a frame for the icon
    icon_frame = tk.Frame(frame, bg="white")
    icon_frame.grid(row=0, column=0, padx=(0, 0), pady=(50, 0))

    # Create the icon label,
    icon_label = tk.Label(icon_frame, image=icon1, bg="white", wraplength=label_width)
    icon_label.image = icon1
    icon_label.pack(side=tk.LEFT)

    count_frame = tk.Frame(frame, bg="white")
    count_frame.grid(row=1, column=0, columnspan=2, pady=(0, 20))

    count_label = tk.Label(count_frame, text="0", font=URW_Geometric_Count_Daily, bg="white", fg="#1F2E46", wraplength=label_width)
    count_label.pack(side=tk.LEFT, padx=(20, 0), pady=(65, 0))

    paned_window.paneconfig(frame, minsize=label_width)

    # Create widgets for the second column (image)
    column2_frame = tk.Frame(paned_window, bg="white")
    column2_frame.pack(fill=tk.BOTH, expand=True)
    image = PIL.ImageTk.PhotoImage(PIL.Image.open(os.path.join(script_dir, 'assets', 'bike.png')))
    image_label = tk.Label(column2_frame, image=image, width=label_width, height=(height * 3) / 4, bg="white")
    image_label.image = image
    image_label.pack(side=tk.BOTTOM, fill=tk.X)
    paned_window.add(column2_frame, stretch="always")

    paned_window.paneconfig(column2_frame, minsize=label_width)

    # Create widgets for the third column (icon and text)
    column3_frame = tk.Frame(paned_window, bg="white")
    column3_frame.pack(fill=tk.BOTH, expand=True, side=tk.RIGHT)

    icon3 = PIL.ImageTk.PhotoImage(PIL.Image.open(os.path.join(script_dir, 'assets', 'Yıllık_Frame.png')))

    icon3_label = tk.Label(column3_frame, image=icon3, bg="white", wraplength=label_width)
    icon3_label.image = icon3
    icon3_label.grid(row=0, column=0, padx=(85, 25), pady=(50, 0))

    count3_label = tk.Label(column3_frame, text="0", font=URW_Geometric_Count_Yearly, bg="white", fg="#1F2E46", wraplength=label_width)
    count3_label.grid(row=1, column=0, columnspan=2, padx=(85, 0), pady=(65, 0))

    paned_window.paneconfig(column3_frame, minsize=label_width)

    def update_count_label():
        with total_count_lock:
            if total_count > 99:
                URW_Geometric_Count_Daily.config(size=150)
                count_label.config(font=URW_Geometric_Count_Daily)
            elif total_count > 999:
                URW_Geometric_Count_Daily.config(size=100)
                count_label.config(font=URW_Geometric_Count_Daily)

        count_label.config(text=f"{total_count}")

        with total_year_lock:
            if total_year > 99 and total_year < 1000:
                URW_Geometric_Count_Yearly = font.Font(family="URW Geometric", weight="bold", size=175)
                count3_label.config(font=(URW_Geometric_Count_Yearly))
            elif total_year > 999 and total_year < 10000:
                URW_Geometric_Count_Yearly = font.Font(family="URW Geometric", weight="bold", size=150)
                count3_label.config(font=(URW_Geometric_Count_Yearly))
            elif total_year > 9999 and total_year < 100000:
                URW_Geometric_Count_Yearly = font.Font(family="URW Geometric", weight="bold", size=125)
                count3_label.config(font=(URW_Geometric_Count_Yearly))
            elif total_year > 99999 and total_year < 1000000:
                URW_Geometric_Count_Yearly = font.Font(family="URW Geometric", weight="bold", size=100)
                count3_label.config(font=(URW_Geometric_Count_Yearly))
            elif total_year > 999999 and total_year < 10000000:
                URW_Geometric_Count_Yearly = font.Font(family="URW Geometric", weight="bold", size=75)
                count3_label.config(font=(URW_Geometric_Count_Yearly, 40))

        count3_label.config(text=f"{total_year}")

        counter_window.after(1000, update_count_label)

    update_count_label()

def create_date_time_gui(root, x, y):
    global show_camera_window, total_count, total_count_lock, total_year, total_year_lock
    date_time_window = tk.Toplevel()
    date_time_window.geometry(f"1920x1080+{x}+{y}")

    def on_key_press(event):
        if event.keysym == "q":
            root.quit()

    date_time_window.bind("<Key>", on_key_press)
    date_time_window.title("Counter")
    date_time_window.attributes('-fullscreen', True)
    date_time_window.attributes('-alpha', 0.5)
    date_time_window.state('normal')

    # Fonts
    
    URW_Geometric_Bold = font.Font(family="URW Geometric", name="Geometric-Bold", weight="bold", size=120)
    URW_Geometric_Date = font.Font(family="URW Geometric", name="Geometric-Date", weight="normal", size=120)
    URW_Geometric_Time = font.Font(family="URW Geometric", name="Geometric-Time", weight="bold", size=275)
    URW_Geometric = font.Font(family="URW Geometric", name="Geometric", weight="normal", size=65)

    # getting screen's height in pixels
    height = date_time_window.winfo_screenheight()

    # getting screen's width in pixels
    width = date_time_window.winfo_screenwidth()

    label_width = min(int((width / 2) - 500), 600)

    # create a canvas with a transparent background
    canvas = tk.Canvas(date_time_window, width=width, height=height, highlightthickness=0, bg="white")

    # 1st row
    row1 = tk.PanedWindow(canvas, orient=tk.HORIZONTAL, sashrelief="flat", sashwidth=0, borderwidth=0)
    row1.pack(fill=tk.BOTH, expand=True, padx=(490, 450), pady=(0, 0))

    # Create a frame with a colored background
    frame = tk.Frame(row1, bg="white")
    frame.pack(fill="both", expand=True, padx=(0, 0))

    # Load your icon image
    icon1 = PIL.ImageTk.PhotoImage(PIL.Image.open(os.path.join(script_dir, 'assets', 'Tarih_Frame.png')))
    icon1.image = icon1

    # Create a frame for the icon
    icon_frame = tk.Frame(frame, bg="white")
    icon_frame.grid_propagate(False)
    icon_frame.grid(row=0, column=0, padx=(120, 0), pady=(50, 0))

    # Create the icon label,
    icon_label = tk.Label(icon_frame, image=icon1, bg="white")
    icon_label.image = icon1
    icon_label.pack(side=tk.LEFT)

    date_frame = tk.Frame(frame, bg="white")
    date_frame.grid(row=1, column=0, columnspan=1, pady=(0, 0))

    date = datetime.datetime.now().strftime("%d.%m.%Y")

    date_label = tk.Label(date_frame, text=date, font=URW_Geometric_Date, bg="white", fg="#1F2E46")
    date_label.pack(padx=(150, 0), pady=(65, 0), anchor="center")

    row1.add(frame)

    # row between
    row_between = tk.PanedWindow(canvas, orient=tk.HORIZONTAL, sashrelief="flat", sashwidth=0, borderwidth=0)
    line_frame = tk.Frame(row_between, width=width, height=1, bg="#D1E0EA")
    line_frame.grid(row=0, column=0, columnspan=3, pady=(0, 0), padx=(0, 0))

    row_between.pack(fill="none", expand=True)

    # 2nd row
    paned_window = tk.PanedWindow(canvas, orient=tk.HORIZONTAL, sashrelief="flat", sashwidth=0, borderwidth=0)
    paned_window.pack(fill=tk.BOTH, expand=True, padx=(400, 400), pady=(0, 0))

    # Create a frame with a colored background
    frame2 = tk.Frame(paned_window, bg="white")
    frame2.pack(fill="both", expand=True)
    frame2.columnconfigure(0, weight=1)
    
    # Load your icon image
    icon2 = PIL.ImageTk.PhotoImage(PIL.Image.open(os.path.join(script_dir, 'assets', 'Saat_Frame.png')))
    icon2.image = icon2

    # Create a frame for the icon
    icon_frame_2 = tk.Frame(frame2, bg="white")
    icon_frame_2.grid(row=0, column=0, padx=(15, 0), pady=(50, 0))

    # Create the icon label
    icon_label = tk.Label(icon_frame_2, image=icon2, bg="white")
    icon_label.image = icon2
    icon_label.pack(side=tk.LEFT)

    time_frame = tk.Frame(frame2, bg="white")
    time_frame.grid(row=1, column=0, columnspan=2, pady=(0, 20))

    time = datetime.datetime.now().strftime("%H:%M")

    time_label = tk.Label(time_frame, text=time, font=URW_Geometric_Time, bg="white", fg="#1F2E46")
    time_label.pack(side=tk.LEFT, padx=(20, 0), pady=(65, 0))

    paned_window.add(frame2, minsize=label_width)

    def update_date_time():
        date = datetime.datetime.now().strftime("%d.%m.%Y")
        time = datetime.datetime.now().strftime("%H:%M")

        date_label.config(text=date)
        time_label.config(text=time)

        date_time_window.after(100, update_date_time)

    update_date_time()

    # pack the canvas
    canvas.pack(fill="both", expand=True)

if __name__ == "__main__":
    
    video_thread = threading.Thread(target=video_processing_thread)
    video_thread.daemon = True  # The thread will terminate when the main program exits
    video_thread.start()

    monitors = get_monitors()

    # Create the GUI windows for two monitors
    root = tk.Tk()
    root.withdraw()
    create_date_time_gui(root, monitors[0].x, monitors[0].y)
    root.mainloop()