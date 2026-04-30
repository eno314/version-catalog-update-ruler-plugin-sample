package jp.eno314.vcu.pdate.ruler.sample.configuration

import org.apache.hc.client5.http.config.ConnectionConfig
import org.apache.hc.client5.http.config.RequestConfig
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.core5.util.TimeValue
import org.apache.hc.core5.util.Timeout
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestClient

@Configuration
class RestClientConfig {
    private val connectTimeout = Timeout.ofMilliseconds(500)
    private val connectionRequestTimeout = Timeout.ofMilliseconds(500)
    private val responseTimeout = Timeout.ofMilliseconds(1000)

    private val maxConnTotal = 75
    private val maxConnPerRoute = 20

    private val evictIdleConnectionTime = TimeValue.ofMilliseconds(3000)

    @Bean
    fun restClientBuilder(): RestClient.Builder {
        val connectionConfig =
            ConnectionConfig
                .custom()
                .setConnectTimeout(connectTimeout)
                .build()
        val connectionManager =
            PoolingHttpClientConnectionManagerBuilder
                .create()
                .setDefaultConnectionConfig(connectionConfig)
                .setMaxConnTotal(maxConnTotal)
                .setMaxConnPerRoute(maxConnPerRoute)
                .build()
        val requestConfig =
            RequestConfig
                .custom()
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setResponseTimeout(responseTimeout)
                .build()
        val httpClient =
            HttpClients
                .custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .evictIdleConnections(evictIdleConnectionTime)
                .evictExpiredConnections()
                .build()
        val httpRequestFactory = HttpComponentsClientHttpRequestFactory(httpClient)
        return RestClient.builder().requestFactory(httpRequestFactory)
    }
}
