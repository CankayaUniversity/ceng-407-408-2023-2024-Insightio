package cankaya.insightio.infrastructure.mongodb.impls

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository


// her repo için ayır yazılır
// impl = implementation, neden ismi impl? altında cb methodlarını override edeceğimiz için
class AccountRepositoryImpl {
    // override save, load
    // override executeQuery -> o ne demek_ baya bildiğin "select * from table" query leri çalıştırmak için


}
//Login için repo
@Repository
interface UserRepository : MongoRepository<User, String> {
    fun findByUsername(username: String): User?
    fun findByEmail(email: String): User?
}

//Cameraconf repo
@Repository
interface CameraRepository : MongoRepository<Camera, String> {
    fun findByType(type: CameraType): List<Camera>

    fun findByStatus(status: CameraStatus): List<Camera>

    fun findByName(name: String): Camera?
}