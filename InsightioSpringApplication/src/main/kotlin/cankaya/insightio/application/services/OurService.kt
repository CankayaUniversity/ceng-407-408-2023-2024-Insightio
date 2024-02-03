package cankaya.insightio.application.services
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


