package jp.eno314.vcu.pdate.ruler.sample.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.time.Duration

@Configuration
class RestClientConfig {
    @Bean
    fun restClient(): RestClient {
        val factory =
            SimpleClientHttpRequestFactory().apply {
                setConnectTimeout(Duration.ofSeconds(1))
                setReadTimeout(Duration.ofSeconds(1))
            }
        return RestClient.builder().requestFactory(factory).build()
    }
}
