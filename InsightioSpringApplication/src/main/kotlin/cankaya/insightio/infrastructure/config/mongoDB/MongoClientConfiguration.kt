package cankaya.insightio.infrastructure.config.mongoDB

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration

@Configuration
class MongoClientConfiguration : AbstractMongoClientConfiguration() {
    override fun getDatabaseName(): String {
        return "user-insightio"
    }

    override fun mongoClient(): MongoClient {
        return MongoClients.create("mongodb://localhost:27017")
    }
}