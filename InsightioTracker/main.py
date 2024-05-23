from lib.server import StreamServer
from lib.vthread_manager import VideoThreadManager
from ultralytics import YOLO
import signal
import torch
import time
import sys

api = None
server = None

def initialize_model():
    global model, CLASS_NAMES_DICT

    MODEL = "yolov8l-oiv7.pt"
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

    return model, CLASS_NAMES_DICT

def signal_handler(sig, frame):
    print('Shutting down gracefully...')
    vtm.shutdown()
    server.stop_server()
    sys.exit(0)

if __name__ == "__main__":    
    model, CLASS_NAMES_DICT = initialize_model()

    server = StreamServer()
    server.run_server()

    vtm = VideoThreadManager(model, CLASS_NAMES_DICT, server)
    vtm.start_count_reporter()

    signal.signal(signal.SIGINT, signal_handler)
    signal.signal(signal.SIGTERM, signal_handler)

    while True:
        vtm.fetch_and_update_threads()
        time.sleep(5)
        