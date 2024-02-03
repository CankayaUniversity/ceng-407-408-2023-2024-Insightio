package cankaya.insightio.infrastructure.couchbase.config

import jakarta.validation.constraints.NotBlank
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document


@Configuration
class CouchbaseConfiguration {

    // object mapper falan eklenecek
    // bean oluşturulacak cluster ve bucket için
}



// mongodb user connection
@Document(collection = "user-insightio")
data class User(
    @Id
    val id: String? = null,

    @NotBlank
    val username: String,

    @NotBlank
    val email: String,

    @NotBlank
    val password: String
)
