package cankaya.insightio.infrastructure.mongodb.impls

enum class CameraType {
    CONNECTEDCAMERA,
    IPCAMERA,
}

enum class ZoneType {
    LINE,
    RECTANGLE,
}

enum class CameraStatus {
    DISCONNECTED,
    CONNECTED,
}

// EDA gerekli taşımaları yaptıktan sonra bu config dosyasını silebilir misin
