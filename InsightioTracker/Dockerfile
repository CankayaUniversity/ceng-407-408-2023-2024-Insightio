# Use a Python 3.11 base image
FROM python:3.11

# Set the working directory inside the container
WORKDIR /app

# Copy the Python script and other necessary files
COPY . .

# Get yolov8l-oiv7.pt and place it in the InisghtioTracker directory
# Assuming the yolov8l-oiv7.pt file is available in the current directory
COPY yolov8l-oiv7.pt /app/InisghtioTracker/

# Install dependencies within the virtual environment
RUN python -m venv venv \
    && /app/venv/bin/pip install -r requirements.txt \
    && /app/venv/bin/pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu118 \
    && /app/venv/bin/pip install torchvision==0.16.2+cu118 -f https://download.pytorch.org/whl/torch_stable.html \
    && apt-get update \
    && apt-get install -y libgl1-mesa-glx \
    && rm -rf /var/lib/apt/lists/*

# Set the entry point to run the script
CMD ["/app/venv/bin/python", "main.py"]
