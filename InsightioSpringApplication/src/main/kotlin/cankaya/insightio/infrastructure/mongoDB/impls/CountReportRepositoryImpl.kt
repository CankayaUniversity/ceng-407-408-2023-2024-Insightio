package cankaya.insightio.infrastructure.mongoDB.impls

import cankaya.insightio.application.persistance.CountReportRepository
import cankaya.insightio.domain.CountReport
import com.mongodb.client.model.Filters
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class CountReportRepositoryImpl(private val mongoTemplate: MongoTemplate) : CountReportRepository {
    companion object {
        const val COUNT_REPORT_COLLECTION = "CountReport"
    }

    override fun save(countReport: CountReport): CountReport {
        return mongoTemplate.save(countReport)
    }

    override fun getDistinctTargetIds(): List<Int> {
        return mongoTemplate.getCollection(COUNT_REPORT_COLLECTION)
            .distinct("targetId", Int::class.java)
            .toList()
    }

    override fun getDistinctTargetIdsByCameraId(cameraId: String): List<Int> {
        val filter = Filters.eq("cameraId", cameraId)
        return mongoTemplate.getCollection(COUNT_REPORT_COLLECTION)
            .distinct("targetId", filter, Int::class.java)
            .toList()
    }

    override fun getReportsByCameraIdAndTargetId(
        cameraId: String,
        targetId: Int,
    ): List<CountReport> {
        val query =
            Query()
                .addCriteria(Criteria.where("cameraId").`is`(cameraId).and("targetId").`is`(targetId))
        return mongoTemplate.find(query, CountReport::class.java)
    }

    override fun getReportsByCameraIdAndTargetIdAndDate(
        cameraId: String,
        targetId: Int,
        startDate: Instant,
        endDate: Instant,
    ): List<CountReport> {
        val query =
            Query()
                .addCriteria(
                    Criteria.where("cameraId").`is`(cameraId)
                        .and("targetId").`is`(targetId)
                        .and("timestamp").gte(startDate.toString()).lt(endDate.toString()),
                )
        return mongoTemplate.find(query, CountReport::class.java)
    }

    override fun getReportsByCameraIdAndTargetIdAndDateSorted(
        cameraId: String,
        targetId: Int,
        startDate: Instant,
        endDate: Instant,
    ): List<CountReport> {
        val query =
            Query().apply {
                addCriteria(
                    Criteria.where("cameraId").`is`(cameraId)
                        .and("targetId").`is`(targetId)
                        .and("timestamp").gte(startDate).lt(endDate),
                )
                with(Sort.by(Sort.Direction.ASC, "timestamp"))
            }
        return mongoTemplate.find(query, CountReport::class.java)
    }
}
