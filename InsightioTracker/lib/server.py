import logging
import threading
import asyncio
import cv2
from aiohttp import web
import aiohttp_cors
from lib import config

# Configure logging
logging.basicConfig(level=logging.DEBUG, format='%(asctime)s - %(levelname)s - %(message)s')

class StreamServer:
    def __init__(self, port=config["tracker_server_port"]):
        self.streams = {}
        self.port = port
        self.app = web.Application()
        self.setup_routes()
        self.runner = None
        self.server_thread = None
        self.loop = None

    def setup_routes(self):
        cors = aiohttp_cors.setup(self.app, defaults={
            "http://localhost:5173": aiohttp_cors.ResourceOptions(
                allow_credentials=True,
                expose_headers="*",
                allow_headers="*",
            )
        })
        route = self.app.router.add_get('/{stream_id}', self.handle_get_request)
        cors.add(route)

    async def handle_get_request(self, request):
        stream_id = request.match_info['stream_id']
        logging.debug(f"Received GET request for stream ID: {stream_id}")

        try:
            if stream_id.startswith("frame_"):
                return await self.stream_frame(request, stream_id)  # Stream annotated frame
            elif stream_id.startswith("all_annotated_frame_"):
                return await self.stream_frame(request, stream_id, "all_annotated_frame")  # Stream all annotated frame
            elif stream_id.startswith("raw_frame_"):
                return await self.stream_frame(request, stream_id, "raw_frame")  # Stream raw frame   
            elif stream_id.startswith("current_count_"):
                return await self.send_data(request, stream_id, "current_counts")  # Stream current count
            elif stream_id.startswith("total_count_"):
                return await self.send_data(request, stream_id, "total_counts")  # Stream total count
            else:
                raise web.HTTPNotFound()
        except Exception as e:
            logging.error(f"Error handling GET request: {e}")
            raise web.HTTPInternalServerError()

    async def stream_frame(self, request, stream_id, frame_key="frame"):
        async def frame_generator():
            while True:
                frame = self.streams.get(stream_id, {}).get(frame_key)
                if frame is not None:
                    ret, jpeg = cv2.imencode('.jpg', frame)
                    if not ret:
                        continue
                    yield (b'--frame\r\n'
                           b'Content-Type: image/jpeg\r\n'
                           b'Content-Length: ' + str(len(jpeg)).encode() + b'\r\n'
                           b'\r\n' + jpeg.tobytes() + b'\r\n')
                await asyncio.sleep(0.1)

        headers = {
            'Content-Type': 'multipart/x-mixed-replace; boundary=frame'
        }
        return web.Response(body=frame_generator(), headers=headers)

    async def send_data(self, request, stream_id, data_key):
        data = self.streams.get(stream_id, {}).get(data_key, '0')
        response_data = {'data': data}
        return web.json_response(response_data)

    def run_server(self):
        def run():
            self.loop = asyncio.new_event_loop()
            asyncio.set_event_loop(self.loop)
            self.runner = web.AppRunner(self.app)
            self.loop.run_until_complete(self.runner.setup())
            site = web.TCPSite(self.runner, 'localhost', self.port)
            self.loop.run_until_complete(site.start())
            logging.info(f"Starting server on port {self.port}")
            try:
                self.loop.run_forever()
            finally:
                self.loop.run_until_complete(self.shutdown())

        # Create and start the server thread
        self.server_thread = threading.Thread(target=run)
        self.server_thread.daemon = True
        self.server_thread.start()

    async def shutdown(self):
        logging.info("Shutting down server")
        await self.runner.cleanup()
        self.loop.stop()

    def stop_server(self):
        if self.loop:
            self.loop.call_soon_threadsafe(self.loop.stop)
        if self.server_thread:
            self.server_thread.join()

    def update_stream(self, stream_id, frame=None, current_count=None, total_count=None, frame_key="frame"):
        if stream_id not in self.streams:
            self.streams[stream_id] = {}
        if frame is not None:
            self.streams[stream_id][frame_key] = frame
        if current_count is not None:
            self.streams[stream_id]['current_counts'] = str(current_count)
        if total_count is not None:
            self.streams[stream_id]['total_counts'] = str(total_count)

server = StreamServer()