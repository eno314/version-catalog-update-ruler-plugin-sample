package jp.eno314.vcu.pdate.ruler.sample.infrastructure

import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder

private const val GOOGLE_BOOKS_BASE_URL = "https://www.googleapis.com/books/v1/volumes"

@Component
class GoogleBooksClient(
    private val restClientBuilder: RestClient.Builder,
) {
    fun search(request: GoogleBooksSearchRemoteRequest): GoogleBooksSearchRemoteResponse {
        val uriBuilder =
            UriComponentsBuilder
                .newInstance()
                .uri(java.net.URI.create(GOOGLE_BOOKS_BASE_URL))
                .queryParam("q", request.query)

        request.googleApiKey?.let { uriBuilder.queryParam("key", it) }

        uriBuilder
            .queryParam("printType", request.printType)
            .queryParam("langRestrict", request.langRestrict)

        val uri = uriBuilder.build().encode().toUri()

        return restClientBuilder
            .build()
            .get()
            .uri(uri)
            .retrieve()
            .body(GoogleBooksSearchRemoteResponse::class.java)
            ?: GoogleBooksSearchRemoteResponse(kind = null, totalItems = 0, items = emptyList())
    }
}
