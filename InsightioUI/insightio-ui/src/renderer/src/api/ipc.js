export function minimize() {
    window.api.send('minimize-window')
}

export function close() {
    window.api.send('close-window')
}