package cankaya.insightio

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@OpenAPIDefinition(
    info =
        Info(
            title = "Insightio Swagger",
            description = "Insightio's spring boot application",
            contact =
                Contact(
                    name = "Team 6",
                    url = "https://cankayauniversity.github.io/ceng-407-408-2023-2024-Insightio/",
                    email = "c2011030@student.cankaya.edu.tr",
                ),
            version = "1.0",
        ),
)
@SpringBootApplication
@EnableMongoRepositories(basePackages = ["cankaya.insightio.infrastructure"])
class InsightioApplication : SpringBootServletInitializer()

fun main(args: Array<String>) {
    runApplication<InsightioApplication>(*args)
}
