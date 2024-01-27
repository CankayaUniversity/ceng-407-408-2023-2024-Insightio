### Prerequisites

1. The following build is tested on a Windows 11 machine and is designed to work on Windows 10 and 11 machines alone.

2. Python 3.11 (tested on Python 3.11.7) is needed to build the project. You can download the tested
version of Python from [here](https://www.python.org/downloads/release/python-3117/). You can use 
versions 3.11 and above to run the script but 3.11 is necessary for building.

### Build Instructions

1. Get `yolov8x.pt` from [here](https://github.com/ultralytics/assets/releases/download/v8.1.0/yolov8x-oiv7.pt) and place the file under `InisightioTracker/`.

2. In the root (`InisghtioTracker/`) directory, run the following commands with a `PowerShell` session:

```powershell
# Create a virtual environment
py -3.11 -m venv venv

# Activate virtual environment
.\venv\Scripts\activate

# Install dependencies
pip install -r requirements.txt

# Optional: Deactivate the virtual environment if you won't be running the script
.\venv\Scripts\deactivate.bat
```

### Running The Script

```powershell
# Optional: If you have any troubles running a PowerShell script run the following
Set-ExecutionPolicy RemoteSigned

# Activate virtual environment
.\venv\Scripts\activate

# Run the script
py .\main.py

# Or run the unstable version (for experimenting and development)
py .\main.altered.py

# When you are done, don't forget to deactivate the virtual environment
.\venv\Scripts\deactivate.bat
```
