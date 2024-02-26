export const ffServerURL = "http://localhost:9292"

export function minimize() {
    window.api.send('minimize-window')
}

export function close() {
    window.api.send('close-window')
}

export function startRtspStream(rtspUrl) {
    window.api.send('start-rtsp-stream', rtspUrl)
}

export function stopRtspStream() {
    window.api.send('stop-rtsp-stream')
}