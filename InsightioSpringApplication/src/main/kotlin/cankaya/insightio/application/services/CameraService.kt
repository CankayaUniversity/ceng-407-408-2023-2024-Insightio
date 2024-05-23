package cankaya.insightio.application.services

import cankaya.insightio.application.persistance.CameraRepository
import cankaya.insightio.domain.Camera
import cankaya.insightio.infrastructure.mongoDB.config.CameraStatus
import cankaya.insightio.infrastructure.mongoDB.config.CameraType
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
