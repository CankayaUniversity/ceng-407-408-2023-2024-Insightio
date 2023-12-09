### Prerequisites

1. The following build is tested on a Windows 11 machine and is designed to work on Windows 10 and 11 machines alone.

2. Python3 is needed to build and run the project.

### Build Instructions

1. Get `yolov8x.pt` from [here](https://github.com/ultralytics/assets/releases/download/v0.0.0/yolov8x.pt) and place the file under both `InisightioTracker/` and `InsightioTracker/ByteTrack/`.

2. In the root (`InisghtioTracker/`) directory, run the following commands with a `PowerShell` session:

```powershell
# Create a virtual environment
py -m venv venv

# Activate virtual environment
.\venv\Scripts\activate

# Optional 1: If you have any troubles running a PowerShell script run the following
Set-ExecutionPolicy RemoteSigned

# Install specific pip version
py -m pip install pip==21.1.1

# Install dependencies
pip install -r requirements.txt

# Navigate to ByteTrack
cd ByteTrack

# Install dependencies for byte tracker
pip install -r requirements.txt

# Navigate back to the root folder
cd ..

# Optional 2: If the build doesnt work uninstall opencv-python...
pip uninstall opencv-python

# ... then reinstall it
pip install opencv-python

# Run the script
py .\main.altered.py

# Optional 3: When you are done, you can deactivate the virtual environment
.\venv\Scripts\deactivate.bat
```
