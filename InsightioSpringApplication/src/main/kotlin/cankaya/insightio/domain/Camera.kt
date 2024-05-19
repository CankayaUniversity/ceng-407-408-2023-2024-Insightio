package cankaya.insightio.domain

import cankaya.insightio.infrastructure.mongodb.impls.CameraStatus
import cankaya.insightio.infrastructure.mongodb.impls.CameraType
import jakarta.validation.constraints.NotBlank
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

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

// EDA CameraStatus ve CameraType'ı buraya declare edebilir misin
