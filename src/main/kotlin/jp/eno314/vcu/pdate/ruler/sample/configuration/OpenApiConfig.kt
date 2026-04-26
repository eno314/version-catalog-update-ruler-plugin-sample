package jp.eno314.vcu.pdate.ruler.sample.configuration

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun customOpenAPI(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("RSS API")
                    .version("1.0.0")
                    .description("API for fetching and processing RSS feeds."),
            )
}
