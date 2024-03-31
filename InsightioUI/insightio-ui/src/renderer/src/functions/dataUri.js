export default function readWriteDataUri(file, callback) {
    const reader = new FileReader()
    reader.onload = (e) => {
        callback(e)
    }
    reader.readAsDataURL(file)
}