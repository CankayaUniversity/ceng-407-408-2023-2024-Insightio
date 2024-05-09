package cankaya.insightio.infrastructure.mongodb.impls

// mongodb user connection
// TODO bunlar config değil, bu paketin içinde olmamalılar, DTO objeleri bunlar
// DTO = data transfer object, db deki objeyi temsil eder, domain objeleri de değilerdir, bunu mongoDb  models altına geçirebilir misin?

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
