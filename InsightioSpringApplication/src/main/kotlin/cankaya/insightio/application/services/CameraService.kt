package cankaya.insightio.application.services

import cankaya.insightio.infrastructure.mongoDB.impls.CameraRepository
import cankaya.insightio.infrastructure.mongodb.impls.Camera
import cankaya.insightio.infrastructure.mongodb.impls.CameraStatus
import cankaya.insightio.infrastructure.mongodb.impls.CameraType
import org.springframework.stereotype.Service

@Service
class CameraService(private val cameraRepository: CameraRepository) {
    fun findAllCameras(): List<Camera> = cameraRepository.findAll()

    fun findCameraById(id: String): Camera? = cameraRepository.findById(id).orElse(null)

    fun createCamera(camera: Camera): Camera = cameraRepository.save(camera)

    fun updateCamera(camera: Camera): Camera = cameraRepository.save(camera)

    fun deleteCamera(id: String) = cameraRepository.deleteById(id)

    fun findByType(type: CameraType): List<Camera> = cameraRepository.findByType(type)

    fun findByStatus(status: CameraStatus): List<Camera> = cameraRepository.findByStatus(status)

    fun findByName(name: String): Camera? = cameraRepository.findByName(name)
}
