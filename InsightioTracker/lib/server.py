from http.server import BaseHTTPRequestHandler, HTTPServer
from lib import config
import socketserver
import threading
import time
import cv2

class StreamingHandler(BaseHTTPRequestHandler):
    def __init__(self, streams, *args, **kwargs):
        self.streams = streams
        super().__init__(*args, **kwargs)

    def do_GET(self):
        # Extract stream ID from the URL
        stream_id = self.path.strip("/")

        if stream_id.startswith("frame_"):
            self.stream_frame(stream_id, False)  # Stream annotated frame
        elif stream_id.startswith("all_annotated_frame_"):
            self.stream_frame(stream_id, True)  # Stream all annotated frame
        elif stream_id.startswith("current_count_"):
            self.send_data(stream_id, "current_counts")  # Stream current count
        elif stream_id.startswith("total_count_"):
            self.send_data(stream_id, "total_counts")  # Stream total count
        else:
            self.send_error(404, "Not Found")

    def stream_frame(self, stream_id, is_all_annotated_frame):
        self.send_response(200)
        self.send_header('Content-type', 'multipart/x-mixed-replace; boundary=frame')
        self.end_headers()

        frame_key = 'all_annotated_frame' if is_all_annotated_frame else 'frame'
        while True:
            frame = self.streams.get(stream_id, {}).get(frame_key)
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
        data = self.streams.get(stream_id, {}).get(data_key, '0')
        self.wfile.write(data.encode())

class ThreadedHTTPServer(socketserver.ThreadingMixIn, HTTPServer):
    """Handle requests in a separate thread."""

def handler_factory(streams):
    def create_handler(*args, **kwargs):
        return StreamingHandler(streams, *args, **kwargs)
    return create_handler

class StreamServer:
    def __init__(self):
        self.streams = {}
        self.port = config["tracker_server_port"]
        self.server_thread = None

    def run_server(self):
        def server_thread():
            server_address = ('', self.port)
            httpd = ThreadedHTTPServer(server_address, handler_factory(self.streams))
            try:
                httpd.serve_forever()
            except KeyboardInterrupt:
                pass
            finally:
                httpd.server_close()

        # Create and start the server thread
        self.server_thread = threading.Thread(target=server_thread)
        self.server_thread.daemon = True
        self.server_thread.start()

    def update_stream(self, stream_id, frame=None, current_count=None, total_count=None, frame_key="frame"):
        if stream_id not in self.streams:
            self.streams[stream_id] = {}
        if frame is not None:
            self.streams[stream_id][frame_key] = frame
        if current_count is not None:
            self.streams[stream_id]['current_counts'] = str(current_count)
        if total_count is not None:
            self.streams[stream_id]['total_counts'] = str(total_count)