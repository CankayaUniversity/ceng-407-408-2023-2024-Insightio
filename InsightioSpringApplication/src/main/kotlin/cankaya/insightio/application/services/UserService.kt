package cankaya.insightio.application.services


import cankaya.insightio.infrastructure.mongodb.impls.*
import jakarta.xml.bind.DatatypeConverter
import org.springframework.stereotype.Service
import java.security.MessageDigest

//Login için service

//typelara dikkat et database ve burdan gönderilenler için
@Service
class UserService(private val userRepository: UserRepository) {

    fun validateUser(login: String, inputPassword: String): Boolean {
        val user = userRepository.findByUsername(login) ?: userRepository.findByEmail(login)
        if (user != null) {
            val hashedInputPassword = hashPassword(inputPassword)
            return hashedInputPassword.equals(user.password)
        }
        return false
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
        return DatatypeConverter.printHexBinary(hash).uppercase()
    }
}




//Camera Conf Service -> TODO bunu CameraService gibi ayrı dosyaya çıkartır mısın
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