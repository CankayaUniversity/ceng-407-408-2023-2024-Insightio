from lib import config
import requests

class TrackerAPI:
    def __init__(self):
        self.URL_BASE = config["api_url_base"]

    def get_camera_settings(self):
        url = f"{self.URL_BASE}/cameras/all"
        try:
            response = requests.get(url)
            if response.status_code == 200:
                return response.json()
            else:
                print(f"Failed to get camera settings. Status code: {response.status_code}")
                return None
        except requests.exceptions.RequestException as e:
            print(f"Error while fetching camera settings: {e}")
            return None
    
    def post_count_reports(self, report):
        url = f"{self.URL_BASE}/countReport"  # Replace with actual endpoint
        try:
            response = requests.post(url, json=report)
            if response.status_code == 200:
                print("Report posted successfully.")
            else:
                print(f"Failed to post report. Status code: {response.status_code}")
        except requests.exceptions.RequestException as e:
            print(f"Error while posting count report: {e}")