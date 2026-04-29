package jp.eno314.vcu.pdate.ruler.sample.infrastructure

import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class RssClient(
    private val restClientBuilder: RestClient.Builder,
) {
    fun fetch(request: RssFetchRemoteRequest): RssFetchRemoteResponse {
        val xml =
            restClientBuilder
                .build()
                .get()
                .uri(request.uri)
                .retrieve()
                .body(String::class.java)
                ?: ""

        return RssFetchRemoteResponse(rawXml = xml)
    }
}
