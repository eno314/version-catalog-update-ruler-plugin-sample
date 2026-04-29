package jp.eno314.vcu.pdate.ruler.sample.infrastructure

import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
open class RssClient(
    private val restClient: RestClient,
) {
    open fun fetch(request: RssFetchRemoteRequest): RssFetchRemoteResponse {
        val xml =
            restClient
                .get()
                .uri(request.uri)
                .retrieve()
                .body(String::class.java)
                ?: ""

        return RssFetchRemoteResponse(rawXml = xml)
    }
}
