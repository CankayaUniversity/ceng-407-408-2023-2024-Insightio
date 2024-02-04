### Prerequisites

1. The following build is tested on a Windows 11 machine and is designed to work on Windows 10 and 11 machines alone.

2. Python 3.11 (tested on Python 3.11.7) is needed to build the project. You can download the tested
version of Python from [here](https://www.python.org/downloads/release/python-3117/). You can use 
versions 3.11 and above to run the script but 3.11 is necessary for building.

3. GPU Usage

### Setting up CUDA (For GPU Usage)

1. Ensure you have NVIDIA GPU.
   
3. Download [CUDA Toolkit](https://developer.nvidia.com/cuda-toolkit-archive) based on which Compute Platform that [Pytorch](https://pytorch.org/get-started/locally/) requires.

4. Download [cuDNN](https://developer.nvidia.com/rdp/cudnn-archive) that has same version with your CUDA Toolkit.

5. Copy the contents of each bin, include and lib files from cuDNN and paste it to CUDA's bin, include and lib files.

6. Verify CUDA_PATH and CUDA_PATH_VX_Y exists by checking the Environment Variables from your pc.

7. Verify CUDA installation from your VS Code Terminal with this command :
 ```powershell
 nvcc --version
 ```

9. If CUDA installation is not recognized by VS Code, add this setting to '.vscode/settings.json':

 ```powershell
 {
     "terminal.integrated.env.windows": {
         "PATH": "${env:PATH};C:\\Program Files\\NVIDIA GPU Computing Toolkit\\CUDA\\vX.Y\\bin"
     }
 }

 ```


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

# Install PyTorch (based on your CUDA Compute Platform)
pip3 install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu118

# Optional: If you have 'torchvision::nms' error caused from 'CUDA' backend, then torchvision might be using cpu. Check with:
print(torchvision.__version__)

# To solve this issue above, use below command for torchvision that works with CUDA:
pip install torchvision==0.16.2+cu118 -f https://download.pytorch.org/whl/torch_stable.html

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
py .\main-altered.py

# When you are done, don't forget to deactivate the virtual environment
.\venv\Scripts\deactivate.bat
```
