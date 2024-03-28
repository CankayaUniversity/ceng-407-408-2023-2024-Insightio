package cankaya.insightio.infrastructure.mongodb.impls

import jakarta.validation.constraints.NotBlank
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

// mongodb user connection
// TODO bunlar config değil, bu paketin içinde olmamalılar, DTO objeleri bunlar
// DTO = data transfer object, db deki objeyi temsil eder, domain objeleri de değilerdir, bunu mongoDb  models altına geçirebilir misin?
@Document(collection = "Users")
data class User(
    @Id
    val id: String? = null,
    @NotBlank
    val username: String,
    @NotBlank
    val email: String,
    @NotBlank
    val password: String,
    val organizationId: String? = null,
    val isAdmin: Boolean = false,
    val isCreate: Boolean = false,
    val createDate: String? = null,
    val createdBy: String? = null,
    val metadata: List<Metadata>? = null,
)

// Mongodb Camera Conf Connection
@Document(collection = "CameraSettings")
data class Camera(
    @Id
    val id: String? = null,
    @NotBlank
    val name: String,
    val type: CameraType,
    @NotBlank
    val ipAddress: String,
    val deviceIndex: Int,
    val status: CameraStatus,
    val targets: Map<Int, List<Zone>>,
    @NotBlank
    val resolution: String,
    @NotBlank
    val createdDate: String,
    @NotBlank
    val createdBy: String,
    val metadata: List<Metadata>,
)

@Document(collection = "MetaData")
data class Metadata(
    @Id
    val categoryId: String,
    val value: String,
)

@Document(collection = "MetadataCategory")
data class MetadataCategory(
    @Id
    val id: String? = null,
    val categoryName: String,
)

data class Zone(
    val zoneName: String,
    val zoneType: ZoneType,
    val startPoint: Point,
    val endPoint: Point,
)

data class Point(
    val x: Float,
    val y: Float,
)

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
