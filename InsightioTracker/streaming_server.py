from http.server import BaseHTTPRequestHandler, HTTPServer
import socketserver
import cv2
import threading
import time

# Centralized streams array
streams = {}

class StreamingHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        # Extract stream ID from the URL
        stream_id = self.path.strip("/")

        if stream_id.startswith("frame_"):
            self.stream_frame(stream_id, False)  # Stream annotated frame
        elif stream_id.startswith("raw_frame_"):
            self.stream_frame(stream_id, True)  # Stream raw frame
        elif stream_id.startswith("current_count_"):
            self.send_data(stream_id, "current_counts")  # Stream current count
        elif stream_id.startswith("total_count_"):
            self.send_data(stream_id, "total_counts")  # Stream total count
        else:
            self.send_error(404, "Not Found")

    def stream_frame(self, stream_id, is_raw):
        self.send_response(200)
        self.send_header('Content-type', 'multipart/x-mixed-replace; boundary=frame')
        self.end_headers()

        frame_key = 'raw_frame' if is_raw else 'frame'
        while True:
            frame = streams.get(stream_id, {}).get(frame_key)
            if frame is not None:
                ret, jpeg = cv2.imencode('.jpg', frame)
                if not ret:
                    continue
                self.wfile.write(b'--frame\r\n')
                self.send_header('Content-Type', 'image/jpeg')
                self.send_header('Content-Length', str(len(jpeg)))
                self.end_headers()
                self.wfile.write(jpeg.tobytes())
                self.wfile.write(b'\r\n')
            time.sleep(0.1)

    def send_data(self, stream_id, data_key):
        self.send_response(200)
        self.send_header('Content-type', 'text/plain')
        self.send_header('Access-Control-Allow-Origin', '*')
        self.end_headers()
        data = streams.get(stream_id, {}).get(data_key, '0')
        self.wfile.write(data.encode())

class ThreadedHTTPServer(socketserver.ThreadingMixIn, HTTPServer):
    """Handle requests in a separate thread."""

def start_streaming_server(port=65444):
    server_address = ('', port)
    httpd = ThreadedHTTPServer(server_address, StreamingHandler)
    httpd.serve_forever()

def run_server():
    server_thread = threading.Thread(target=start_streaming_server)
    server_thread.daemon = True
    server_thread.start()

def update_stream(stream_id, frame=None, current_count=None, total_count=None, is_raw=False):
    if stream_id not in streams:
        streams[stream_id] = {}
    if frame is not None:
        frame_key = 'raw_frame' if is_raw else 'frame'
        streams[stream_id][frame_key] = frame
    if current_count is not None:
        streams[stream_id]['current_counts'] = str(current_count)
    if total_count is not None:
        streams[stream_id]['total_counts'] = str(total_count)