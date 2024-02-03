package cankaya.insightio.application.services
import cankaya.insightio.infrastructure.couchbase.config.Camera
import cankaya.insightio.infrastructure.couchbase.config.CameraStatus
import cankaya.insightio.infrastructure.couchbase.config.CameraType
import cankaya.insightio.infrastructure.couchbase.impls.CameraRepository
import cankaya.insightio.infrastructure.couchbase.impls.UserRepository
import org.springframework.stereotype.Service

// burası application -> services
// servislerimiz burada olucak, sende kendininkini account servis gibi yazabilirsin

class OurService {
    fun ornekFunc() {
        val x = 1
    }
}


//Login için service

@Service
class UserService(private val userRepository: UserRepository) {
    fun validateUser(login: String, password: String): Boolean {
        val user = userRepository.findByUsername(login) ?: userRepository.findByEmail(login)
        return user?.password == password
    }
}



//Camera Conf Service
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



