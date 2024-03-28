package cankaya.insightio.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "MetaData")
data class Metadata(
    @Id
    val categoryId: String,
    val value: String,
)
