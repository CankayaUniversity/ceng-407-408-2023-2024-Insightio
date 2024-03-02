package cankaya.insightio.infrastructure.mongodb.impls

import jakarta.validation.constraints.NotBlank
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Objects


@Configuration
class CouchbaseConfiguration {

    // object mapper falan eklenecek
    // bean oluşturulacak cluster ve bucket için
}



// mongodb user connection
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