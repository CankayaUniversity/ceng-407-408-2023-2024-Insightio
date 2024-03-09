package cankaya.insightio.infrastructure.mongodb.impls

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

// bunlar impls = implementations değil, bunlar interface. bunların implementasyonları bu pakette olmalı
// eğer implementasyon gerekmiyorsa bu iki repository için 2 farklı dosya oluşturup, paketin ismini repository olarak değiştirir misin

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