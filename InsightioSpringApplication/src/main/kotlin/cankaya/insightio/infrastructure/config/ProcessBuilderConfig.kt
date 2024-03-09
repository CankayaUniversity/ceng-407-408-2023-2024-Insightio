package cankaya.insightio.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProcessBuilderConfig {
    @Bean
    fun processBuilder(): ProcessBuilder {
        return ProcessBuilder()
    }
}
