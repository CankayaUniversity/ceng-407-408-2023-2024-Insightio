package cankaya.insightio.application.services


import cankaya.insightio.infrastructure.mongodb.impls.*
import jakarta.xml.bind.DatatypeConverter
import org.springframework.stereotype.Service
import java.security.MessageDigest

//Login için service

//typelara dikkat et database ve burdan gönderilenler için
@Service
class UserService(private val userRepository: UserRepository, private val encryptionUtils: EncryptionUtils) {

    fun validateAndDecryptUser(username: String, hashedInputPassword: String): Boolean {
        val user = userRepository.findByUsername(username) ?: return false

        // Veritabanında AES ile şifrelenmiş şifreyi çözüp, hashleyerek gelen hash ile karşılaştır
        val decryptedPassword = encryptionUtils.decryptAES(user.encryptedPassword, "AES_KEY")
        val decryptedPasswordHashed = hashPassword(decryptedPassword)
        
        return decryptedPasswordHashed == hashedInputPassword
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
        return DatatypeConverter.printHexBinary(hash).uppercase()
    }
}

// EncryptionUtils classı sonradan değiştirilmeli yeri şu anlık burda duruyor
//dependencies eklenmeli eksik**

@Component
class EncryptionUtils {

    fun sha256(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hash = md.digest(input.toByteArray())
        return DatatypeConverter.printHexBinary(hash)
    }

    fun encryptAES(input: String, secretKey: String): ByteArray {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val key = SecretKeySpec(secretKey.toByteArray(), "AES")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return cipher.doFinal(input.toByteArray())
    }

    fun decryptAES(input: ByteArray, secretKey: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val key = SecretKeySpec(secretKey.toByteArray(), "AES")
        cipher.init(Cipher.DECRYPT_MODE, key)
        val decrypted = cipher.doFinal(input)
        return String(decrypted)
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
