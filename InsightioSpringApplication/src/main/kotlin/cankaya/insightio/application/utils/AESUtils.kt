package cankaya.insightio.application.utils

import org.springframework.stereotype.Component
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@Component
class AESUtils {
    companion object {
        private val AES_KEY = "xPn3s6v9yEE&E)H@McQfTjWnZr4t7w!z"
        private val SECRET_KEY = SecretKeySpec(AES_KEY.toByteArray(), "AES")
    }

    fun ByteArray.encodeToBase64(): String {
        return Base64.getEncoder().encodeToString(this)
    }

    fun String.decodeBase64(): ByteArray {
        return Base64.getDecoder().decode(this)
    }

    fun encryptAES(input: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY)
        val encryptedBytes = cipher.doFinal(input.toByteArray())
        return encryptedBytes.encodeToBase64()
    }

    fun decryptAES(inputBase64: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY)
        val decryptedBytes = cipher.doFinal(inputBase64.decodeBase64())
        return String(decryptedBytes)
    }
}
