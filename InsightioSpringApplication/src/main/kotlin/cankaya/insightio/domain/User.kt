package cankaya.insightio.domain

import jakarta.validation.constraints.NotBlank

data class User( // applicationdaki endpointler bunu kullansın
    val id: String? = null,
    @NotBlank
    val username: String,
    @NotBlank
    val email: String,
    @NotBlank
    val password: String,
)
