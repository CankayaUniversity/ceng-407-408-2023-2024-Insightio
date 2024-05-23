package cankaya.insightio.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "MetadataCategory")
data class MetadataCategory(
    @Id
    val id: String? = null,
    val categoryName: String,
)
