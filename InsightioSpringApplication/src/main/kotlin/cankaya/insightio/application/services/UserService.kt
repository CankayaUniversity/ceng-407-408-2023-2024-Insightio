package cankaya.insightio.application.services

import cankaya.insightio.application.utils.AESUtils
import cankaya.insightio.infrastructure.mongodb.impls.*
import org.springframework.stereotype.Service

// Login için service

// typelara dikkat et database ve burdan gönderilenler için
@Service
class UserService(
    private val userRepository: UserRepository,
    private val utils: AESUtils,
) {
    fun validateUser(
        username: String,
        input: String,
    ): Boolean {
        val user = userRepository.findByUsername(username) ?: return false
        val decryptedPassword = utils.decryptAES(user.password)
        return (decryptedPassword == input)
    }

    fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }
}

// dependencies eklenmeli eksik**

// Camera Conf Service -> TODO bunu CameraService gibi ayrı dosyaya çıkartır mısın
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
