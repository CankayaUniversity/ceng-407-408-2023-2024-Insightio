package cankaya.insightio.domain

import jakarta.validation.constraints.NotBlank
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "Users")
data class User(
    @Id
    val id: String? = null,
    @NotBlank
    val username: String,
    @NotBlank
    val email: String,
    @NotBlank
    val password: String,
    val organizationId: String? = null,
    val isAdmin: Boolean = false,
    val isCreate: Boolean = false,
    val createDate: String? = null,
    val createdBy: String? = null,
    val metadata: List<Metadata>? = null,
)
