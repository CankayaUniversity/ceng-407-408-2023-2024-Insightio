package cankaya.insightio.infrastructure.mongoDB.impls

import cankaya.insightio.infrastructure.mongodb.impls.Camera
import cankaya.insightio.infrastructure.mongodb.impls.CameraStatus
import cankaya.insightio.infrastructure.mongodb.impls.CameraType
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

// Cameraconf repo
@Repository
interface CameraRepository : MongoRepository<Camera, String> {
    fun findByType(type: CameraType): List<Camera>

    fun findByStatus(status: CameraStatus): List<Camera>

    fun findByName(name: String): Camera?
}
