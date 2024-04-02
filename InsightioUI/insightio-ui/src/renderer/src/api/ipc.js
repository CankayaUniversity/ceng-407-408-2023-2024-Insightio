export const videoServerUrl = "http://localhost:5175/"

export function minimize() {
    window.api.send('minimize-window')
}

export function close() {
    window.api.send('close-window')
}

export function startStream(streamUrl, callback = null) {
    window.api.send('start-stream', streamUrl);

    // If stream-started is received, then msg is stream ID
    window.api.once('stream-started', (msg) => {
        if (callback) {
            // Second argument indicates success
            callback(msg, true);
        }
    });

    // If ffmpeg-error is received then msg is an error message
    window.api.once('ffmpeg-error', (msg) => {
        if (callback) {
            // Second argument indicates success
            callback(msg, false);
        }
    });
}

export function stopStream(streamId, callback = null) {
    window.api.send('stop-stream', streamId);

    window.api.once('stream-stopped', (streamId) => {
        if (callback) {
            callback(streamId);
        }
    });
}

export function stopAllStreams(callback = null) {
    window.api.send('stop-all-streams');

    window.api.once('all-streams-stopped', (event) => {
        if (callback) {
            callback(event);
        }
    });
}