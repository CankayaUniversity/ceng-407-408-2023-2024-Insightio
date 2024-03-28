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

    fun createUser(user: User): User {
        val encryptedPassword = utils.encryptAES(user.password)
        val newUser = user.copy(password = encryptedPassword)
        return userRepository.save(newUser)
    }

    fun updateUser(
        id: String,
        updatedUser: User,
    ): User {
        val user = findUserById(id) ?: throw Exception("User not found")
        val encryptedPassword = utils.encryptAES(updatedUser.password)

        // metadata update
        val updatedMetadata =
            updatedUser.metadata?.map { metaData ->
                Metadata(
                    categoryId = metaData.categoryId,
                    value = metaData.value,
                )
            }

        // user info
        val userToUpdate =
            user.copy(
                username = updatedUser.username,
                password = encryptedPassword,
                organizationId = updatedUser.organizationId,
                email = updatedUser.email,
                isAdmin = updatedUser.isAdmin,
                isCreate = updatedUser.isCreate,
                createDate = updatedUser.createDate,
                createdBy = updatedUser.createdBy,
                metadata = updatedMetadata,
            )
        return userRepository.save(userToUpdate)
    }

    fun deleteUser(id: String) {
        val user = findUserById(id) ?: throw Exception("User not found")
        userRepository.delete(user)
    }

    fun findAllUsers(): List<User> {
        return userRepository.findAll()
    }

    fun findUserById(id: String): User? {
        return userRepository.findById(id).orElse(null)
    }
}
