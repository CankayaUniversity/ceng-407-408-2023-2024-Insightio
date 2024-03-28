package cankaya.insightio.infrastructure.mongodb.impls

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

// Login için repo
@Repository
interface UserRepository : MongoRepository<User, String> {
    fun findByUsername(username: String): User?
}
