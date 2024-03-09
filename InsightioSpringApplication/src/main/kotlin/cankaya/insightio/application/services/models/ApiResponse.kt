package cankaya.insightio.application.services.models

data class ApiResponse(
    val isSuccess: Boolean,
    val statusCode: Int,
    val response: Any? = null,
    val errorMessage: String? = null,
) {
    companion object {
        fun success(responseBody: Any? = null) =
            ApiResponse(
                true,
                200,
                responseBody,
            )

        fun error(
            responseBody: Any? = null,
            statusCode: Int,
            errorMessage: String,
        ) = ApiResponse(
            false,
            statusCode,
            responseBody,
            errorMessage,
        )
    }
}
