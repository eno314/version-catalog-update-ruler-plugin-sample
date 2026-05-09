package jp.eno314.vcu.pdate.ruler.sample.repository

import jp.eno314.vcu.pdate.ruler.sample.infrastructure.GoogleBooksClient
import jp.eno314.vcu.pdate.ruler.sample.infrastructure.GoogleBooksSearchRemoteRequest
import jp.eno314.vcu.pdate.ruler.sample.infrastructure.VolumeRemoteResponse
import org.springframework.stereotype.Repository

@Repository
class GoogleBooksRepository(
    private val googleBooksClient: GoogleBooksClient,
) {
    fun searchByTitle(
        title: String,
        googleBooksApiKey: String,
        author: String?,
        publisher: String?,
        subject: String?,
        printType: String,
        langRestrict: String,
    ): GoogleBooksSearchDto {
        val query = buildQuery(title, author, publisher, subject)
        val remoteRequest =
            GoogleBooksSearchRemoteRequest(
                query = query,
                googleBooksApiKey = googleBooksApiKey,
                printType = printType,
                langRestrict = langRestrict,
            )
        val remoteResponse = googleBooksClient.search(remoteRequest)

        return GoogleBooksSearchDto(
            totalItems = remoteResponse.totalItems,
            books = remoteResponse.items?.map { it.toBookDto() } ?: emptyList(),
        )
    }

    private fun buildQuery(
        title: String,
        author: String?,
        publisher: String?,
        subject: String?,
    ): String =
        buildList {
            add("intitle:$title")
            author?.let { add("inauthor:$it") }
            publisher?.let { add("inpublisher:$it") }
            subject?.let { add("subject:$it") }
        }.joinToString("+")

    private fun VolumeRemoteResponse.toBookDto(): BookDto =
        BookDto(
            id = id,
            title = volumeInfo.title,
            authors = volumeInfo.authors ?: emptyList(),
            publisher = volumeInfo.publisher,
            publishedDate = volumeInfo.publishedDate,
            description = volumeInfo.description,
            thumbnailUrl = volumeInfo.imageLinks?.thumbnail,
            infoLink = volumeInfo.infoLink,
        )
}
