export const ffServerURL = "http://localhost:9292"

export function minimize() {
    window.api.send('minimize-window')
}

export function close() {
    window.api.send('close-window')
}