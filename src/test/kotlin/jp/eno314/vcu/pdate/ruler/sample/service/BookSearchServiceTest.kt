package jp.eno314.vcu.pdate.ruler.sample.service

import io.mockk.every
import io.mockk.mockk
import jp.eno314.vcu.pdate.ruler.sample.repository.BookDto
import jp.eno314.vcu.pdate.ruler.sample.repository.GoogleBooksRepository
import jp.eno314.vcu.pdate.ruler.sample.repository.GoogleBooksSearchDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BookSearchServiceTest {
    private val googleBooksRepository = mockk<GoogleBooksRepository>()
    private val bookSearchService = BookSearchService(googleBooksRepository)

    @Test
    fun `searchBooks should map dto to response correctly`() {
        val request = BookSearchRequest(title = "Clean Code", googleApiKey = "test-key")
        val dto =
            GoogleBooksSearchDto(
                totalItems = 1,
                books =
                    listOf(
                        BookDto(
                            id = "abc123",
                            title = "Clean Code",
                            authors = listOf("Robert C. Martin"),
                            publisher = "Prentice Hall",
                            publishedDate = "2008-08-01",
                            description = "A handbook of agile software craftsmanship.",
                            thumbnailUrl = "https://example.com/thumb.jpg",
                            infoLink = "https://books.google.com/books?id=abc123",
                        ),
                    ),
            )
        every {
            googleBooksRepository.search(
                title = "Clean Code",
                googleApiKey = "test-key",
                author = null,
                publisher = null,
                subject = null,
                printType = "all",
                langRestrict = "ja",
            )
        } returns dto

        val response = bookSearchService.searchBooks(request)

        assertThat(response.totalItems).isEqualTo(1)
        assertThat(response.books).hasSize(1)
        with(response.books[0]) {
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
    fun `searchBooks should pass googleApiKey to repository`() {
        val request = BookSearchRequest(title = "Kotlin", googleApiKey = "my-api-key")
        val dto = GoogleBooksSearchDto(totalItems = 0, books = emptyList())
        every {
            googleBooksRepository.search(
                title = "Kotlin",
                googleApiKey = "my-api-key",
                author = null,
                publisher = null,
                subject = null,
                printType = "all",
                langRestrict = "ja",
            )
        } returns dto

        val response = bookSearchService.searchBooks(request)

        assertThat(response.totalItems).isEqualTo(0)
        assertThat(response.books).isEmpty()
    }

    @Test
    fun `searchBooks should return empty books when dto has no books`() {
        val request = BookSearchRequest(title = "Nonexistent", googleApiKey = "test-key")
        val dto = GoogleBooksSearchDto(totalItems = 0, books = emptyList())
        every {
            googleBooksRepository.search(
                title = "Nonexistent",
                googleApiKey = "test-key",
                author = null,
                publisher = null,
                subject = null,
                printType = "all",
                langRestrict = "ja",
            )
        } returns dto

        val response = bookSearchService.searchBooks(request)

        assertThat(response.totalItems).isEqualTo(0)
        assertThat(response.books).isEmpty()
    }
}
