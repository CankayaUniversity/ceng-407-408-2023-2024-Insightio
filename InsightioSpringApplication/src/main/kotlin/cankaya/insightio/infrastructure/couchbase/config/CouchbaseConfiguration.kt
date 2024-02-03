package cankaya.insightio.infrastructure.couchbase.config

import jakarta.validation.constraints.NotBlank
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date
import java.util.Objects


@Configuration
class CouchbaseConfiguration {

    // object mapper falan eklenecek
    // bean oluşturulacak cluster ve bucket için
}



// mongodb user connection
@Document(collection = "user-insightio")
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
@Document(collection = "CameraConf")
data class Camera(
    @Id
    val id: String?,
    val name: String,
    val type: CameraType,
    val ipAddress: String,
    val status: CameraStatus,
    val zones: List<Zone>,
    val targets: List<Int>,
    val resolution: String,
    val createdDate: Date,
    val createdBy: String,
    val metadata: List<Metadata>
)

data class Metadata(
    val categoryId: String,
    val value: Objects
)

data class Zone(
    val cameraId: String,
    val zoneName: String,
    val target: Int,
    val vertices: List<Point>
)

data class Point(
    val x: Float,
    val y: Float
)
enum class CameraType {
    ConnectedCamera, IPCamera
}

enum class CameraStatus {
    Disconnected, Connected
}

