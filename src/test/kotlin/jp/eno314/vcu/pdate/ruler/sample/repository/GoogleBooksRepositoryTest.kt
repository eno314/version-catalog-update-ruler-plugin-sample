package jp.eno314.vcu.pdate.ruler.sample.repository

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import jp.eno314.vcu.pdate.ruler.sample.infrastructure.GoogleBooksClient
import jp.eno314.vcu.pdate.ruler.sample.infrastructure.GoogleBooksSearchRemoteRequest
import jp.eno314.vcu.pdate.ruler.sample.infrastructure.GoogleBooksSearchRemoteResponse
import jp.eno314.vcu.pdate.ruler.sample.infrastructure.ImageLinksRemoteResponse
import jp.eno314.vcu.pdate.ruler.sample.infrastructure.VolumeInfoRemoteResponse
import jp.eno314.vcu.pdate.ruler.sample.infrastructure.VolumeRemoteResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GoogleBooksRepositoryTest {
    private val googleBooksClient = mockk<GoogleBooksClient>()
    private val googleBooksRepository = GoogleBooksRepository(googleBooksClient)

    @Test
    fun `searchByTitle should pass intitle query and googleApiKey to client`() {
        val requestSlot = slot<GoogleBooksSearchRemoteRequest>()
        every { googleBooksClient.search(capture(requestSlot)) } returns
            GoogleBooksSearchRemoteResponse(kind = null, totalItems = 0, items = emptyList())

        googleBooksRepository.searchByTitle(
            title = "Clean Code",
            googleApiKey = "test-key",
            author = null,
            publisher = null,
            subject = null,
            printType = "all",
            langRestrict = "ja",
        )

        assertThat(requestSlot.captured.query).isEqualTo("intitle:Clean Code")
        assertThat(requestSlot.captured.googleApiKey).isEqualTo("test-key")
        assertThat(requestSlot.captured.printType).isEqualTo("all")
        assertThat(requestSlot.captured.langRestrict).isEqualTo("ja")
    }

    @Test
    fun `searchByTitle should pass null googleApiKey to client when not specified`() {
        val requestSlot = slot<GoogleBooksSearchRemoteRequest>()
        every { googleBooksClient.search(capture(requestSlot)) } returns
            GoogleBooksSearchRemoteResponse(kind = null, totalItems = 0, items = emptyList())

        googleBooksRepository.searchByTitle(
            title = "Clean Code",
            googleApiKey = null,
            author = null,
            publisher = null,
            subject = null,
            printType = "all",
            langRestrict = "ja",
        )

        assertThat(requestSlot.captured.googleApiKey).isNull()
    }

    @Test
    fun `searchByTitle should build complex query and pass parameters to client`() {
        val requestSlot = slot<GoogleBooksSearchRemoteRequest>()
        every { googleBooksClient.search(capture(requestSlot)) } returns
            GoogleBooksSearchRemoteResponse(kind = null, totalItems = 0, items = emptyList())

        googleBooksRepository.searchByTitle(
            title = "Clean Code",
            googleApiKey = "test-key",
            author = "Robert C. Martin",
            publisher = "Prentice Hall",
            subject = "Programming",
            printType = "books",
            langRestrict = "en",
        )

        assertThat(requestSlot.captured.query)
            .isEqualTo("intitle:Clean Code+inauthor:Robert C. Martin+inpublisher:Prentice Hall+subject:Programming")
        assertThat(requestSlot.captured.printType).isEqualTo("books")
        assertThat(requestSlot.captured.langRestrict).isEqualTo("en")
    }

    @Test
    fun `searchByTitle should map remote response to dto correctly`() {
        val remoteResponse =
            GoogleBooksSearchRemoteResponse(
                kind = "books#volumes",
                totalItems = 1,
                items =
                    listOf(
                        VolumeRemoteResponse(
                            id = "abc123",
                            volumeInfo =
                                VolumeInfoRemoteResponse(
                                    title = "Clean Code",
                                    authors = listOf("Robert C. Martin"),
                                    publisher = "Prentice Hall",
                                    publishedDate = "2008-08-01",
                                    description = "A handbook of agile software craftsmanship.",
                                    imageLinks =
                                        ImageLinksRemoteResponse(
                                            smallThumbnail = "https://example.com/small.jpg",
                                            thumbnail = "https://example.com/thumb.jpg",
                                        ),
                                    infoLink = "https://books.google.com/books?id=abc123",
                                ),
                        ),
                    ),
            )
        every { googleBooksClient.search(any()) } returns remoteResponse

        val result =
            googleBooksRepository.searchByTitle(
                title = "Clean Code",
                googleApiKey = "test-key",
                author = null,
                publisher = null,
                subject = null,
                printType = "all",
                langRestrict = "ja",
            )

        assertThat(result.totalItems).isEqualTo(1)
        assertThat(result.books).hasSize(1)
        with(result.books[0]) {
            assertThat(id).isEqualTo("abc123")
            assertThat(title).isEqualTo("Clean Code")
            assertThat(authors).containsExactly("Robert C. Martin")
            assertThat(publisher).isEqualTo("Prentice Hall")
            assertThat(publishedDate).isEqualTo("2008-08-01")
            assertThat(description).isEqualTo("A handbook of agile software craftsmanship.")
            assertThat(thumbnailUrl).isEqualTo("https://example.com/thumb.jpg")
            assertThat(infoLink).isEqualTo("https://books.google.com/books?id=abc123")
        }
    }

    @Test
    fun `searchByTitle should return empty books when items is null`() {
        every { googleBooksClient.search(any()) } returns
            GoogleBooksSearchRemoteResponse(kind = null, totalItems = 0, items = null)

        val result =
            googleBooksRepository.searchByTitle(
                title = "Unknown",
                googleApiKey = "test-key",
                author = null,
                publisher = null,
                subject = null,
                printType = "all",
                langRestrict = "ja",
            )

        assertThat(result.totalItems).isEqualTo(0)
        assertThat(result.books).isEmpty()
    }

    @Test
    fun `searchByTitle should use empty list for null authors`() {
        val remoteResponse =
            GoogleBooksSearchRemoteResponse(
                kind = null,
                totalItems = 1,
                items =
                    listOf(
                        VolumeRemoteResponse(
                            id = "xyz",
                            volumeInfo =
                                VolumeInfoRemoteResponse(
                                    title = "Some Book",
                                    authors = null,
                                    publisher = null,
                                    publishedDate = null,
                                    description = null,
                                    imageLinks = null,
                                    infoLink = null,
                                ),
                        ),
                    ),
            )
        every { googleBooksClient.search(any()) } returns remoteResponse

        val result =
            googleBooksRepository.searchByTitle(
                title = "Some Book",
                googleApiKey = "test-key",
                author = null,
                publisher = null,
                subject = null,
                printType = "all",
                langRestrict = "ja",
            )

        assertThat(result.books[0].authors).isEmpty()
        assertThat(result.books[0].thumbnailUrl).isNull()
    }
}
