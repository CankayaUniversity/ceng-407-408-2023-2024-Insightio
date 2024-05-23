package cankaya.insightio.application.persistance

import cankaya.insightio.domain.Camera
import cankaya.insightio.infrastructure.mongoDB.config.CameraStatus
import cankaya.insightio.infrastructure.mongoDB.config.CameraType
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

// Cameraconf repo
@Repository
interface CameraRepository : MongoRepository<Camera, String> {
    fun findByType(type: CameraType): List<Camera>

    fun findByStatus(status: CameraStatus): List<Camera>

    fun findByName(name: String): Camera?
}
