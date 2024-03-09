package cankaya.insightio.infrastructure.mongodb.impls

import cankaya.insightio.infrastructure.mongoDB.models.CountReport
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

// bende mongo db kurulu değil iskeletini yaptım sadece
@Repository
interface CountRepository : MongoRepository<CountReport, String>
