package cankaya.insightio.infrastructure.mongodb.impls

import jakarta.validation.constraints.NotBlank
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Objects

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
    val password: String
)

//Mongodb Camera Conf Connection
@Document(collection = "CameraSettings")
data class Camera(
    @Id
    val id: String?=null,
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
    val metadata: List<Metadata>
)

data class Metadata(
    val categoryId: String,
    val value: String
)

// TODO bunlar domain objeleri lütfen domain paketinin altında hepsi ayrı dosya olacak şekilde taşıyabilir misiniz?

data class Zone(
    val zoneName: String,
    val zoneType: ZoneType,
    val startPoint: Point,
    val endPoint: Point
)

data class Point(
    val x: Float,
    val y: Float
)

enum class CameraType {
    ConnectedCamera, IPCamera
}

enum class ZoneType {
    Line, Rectangle
}

enum class CameraStatus {
    Disconnected, Connected
}