### Prerequisites

1. The following build is tested on a Windows 11 machine and is designed to work on Windows 10 and 11 machines alone.

2. Python3 is needed to build and run the project.

### Build Instructions

1. Get yolov8x.pt from (here)[https://github.com/ultralytics/assets/releases/download/v0.0.0/yolov8x.pt] and place the file under both `InisightioTracker/` and `InsightioTracker/ByteTrack/`.

2. In the root (`InisghtioTracker/`) directory, run the following commands:

```shell
# Create a virtual environment
py -m venv venv

# Activate virtual environment
.\venv\Scripts\activate

# Install specific pip version
py -m pip install pip==21.1.1

# Install dependencies
pip install -r requirements.txt

# Navigate to ByteTrack
cd ByteTrack

# Install dependencies for byte tracker
pip install -r requirements.txt

cd ..

# Optional: If the build doesnt work reinstall opencv-python
pip uninstall opencv-python

pip install opencv-python

# Run the script
py .\main.altered.py

# When you are done, you can deactivate the virtual environment
.\venv\Scripts\deactivate.bat
```