package cankaya.insightio.infrastructure.mongoDB.impls

import cankaya.insightio.application.persistance.CountReportRepository
import cankaya.insightio.domain.CountReport
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository

@Repository
class CountReportRepositoryImpl(private val mongoTemplate: MongoTemplate) : CountReportRepository {
    override fun save(countReport: CountReport): CountReport {
        return mongoTemplate.save(countReport)
    }
}
