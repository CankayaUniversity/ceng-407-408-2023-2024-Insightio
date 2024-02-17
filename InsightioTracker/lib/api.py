from lib import config
import requests

class TrackerAPI:
    def __init__(self):
        self.URL_BASE = config["api_url_base"]

    def get_camera_settings(self):
        url = f"{self.URL_BASE}/todos/1"
        try:
            response = requests.get(url)
            if response.status_code == 200:
                # Simulated camera settings returned upon successful response
                camera_settings = [
                    {
                        "id": "e7a1174c-42b8-48d1-a359-99ed6f95848b",
                        "Name": "Webcam",
                        "Type": "ConnectedCamera",
                        "IpAddress": "127.0.0.1",
                        "DeviceIndex": 0,
                        "Status": "Active",
                        "Targets": {
                            42: [  # Bicycle target
                                {
                                    "ZoneName": "A",
                                    "ZoneType": 1,
                                    "StartPoint": {"x": 200, "y": 140},
                                    "EndPoint": {"x": 440, "y": 340}
                                },
                                {
                                    "ZoneName": "B",
                                    "ZoneType": 0,
                                    "StartPoint": {"x": 100, "y": 100},
                                    "EndPoint": {"x": 150, "y": 150}
                                }
                            ],
                            21: [  # Banana target
                                {
                                    "ZoneName": "C",
                                    "ZoneType": 0,
                                    "StartPoint": {"x": 120, "y": 350},
                                    "EndPoint": {"x": 280, "y": 350}
                                }
                            ]
                        },
                        "Resolution": "480x640",
                        "CreateDate": "2024-01-28T10:05:40.945Z",
                        "CreatedBy": "1e2f50c5-7dea-46ef-9a86-f4910d5d989f",
                        "Metadata": [
                            {
                                "Key": "67ba4b7d-3ef2-4b5c-ab9b-2e66f3f98730",
                                "Value": None
                            }
                        ]
                    }
                ]
                return camera_settings
            else:
                print(f"Failed to get camera settings. Status code: {response.status_code}")
                return None
        except requests.exceptions.RequestException as e:
            print(f"Error while fetching camera settings: {e}")
            return None
    
    def post_count_reports(self, report):
        url = "http://localhost:4343"  # Replace with actual endpoint
        try:
            response = requests.post(url, json=report)
            if response.status_code == 200:
                print("Report posted successfully.")
            else:
                print(f"Failed to post report. Status code: {response.status_code}")
        except requests.exceptions.RequestException as e:
            print(f"Error while posting count report: {e}")