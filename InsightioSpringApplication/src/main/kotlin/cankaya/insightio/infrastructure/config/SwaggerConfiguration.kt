package cankaya.insightio.infrastructure.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.web.filter.ForwardedHeaderFilter

//@Configuration
//@OpenAPIDefinition(info = Info(title = "Insightio Swagger", description = "API descriptions for the Insightio Project"))
//class SwaggerConfig {
//    @Bean
//    fun customOpenAPI(): OpenAPI {
//        return OpenAPI()
//                .addSecurityItem(SecurityRequirement().addList(AUTHORIZATION))
//                .components(Components().addSecuritySchemes(AUTHORIZATION,
//                        SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
//    }
//
//    @Bean
//    fun forwardedHeaderFilter(): ForwardedHeaderFilter {
//        return ForwardedHeaderFilter()
//    }
//}