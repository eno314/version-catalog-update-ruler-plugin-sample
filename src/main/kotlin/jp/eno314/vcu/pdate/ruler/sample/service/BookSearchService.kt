package jp.eno314.vcu.pdate.ruler.sample.service

import jakarta.validation.Valid
import jp.eno314.vcu.pdate.ruler.sample.repository.GoogleBooksRepository
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

@Service
@Validated
class BookSearchService(
    private val googleBooksRepository: GoogleBooksRepository,
) {
    fun searchBooks(
        @Valid request: BookSearchRequest,
    ): BookSearchResponse {
        val dto =
            googleBooksRepository.searchByTitle(
                title = request.title,
                googleApiKey = request.googleApiKey,
                author = request.author,
                publisher = request.publisher,
                subject = request.subject,
                printType = request.printType,
                langRestrict = request.langRestrict,
            )

        return BookSearchResponse(
            totalItems = dto.totalItems,
            books =
                dto.books.map {
                    BookItem(
                        id = it.id,
                        title = it.title,
                        authors = it.authors,
                        publisher = it.publisher,
                        publishedDate = it.publishedDate,
                        description = it.description,
                        thumbnailUrl = it.thumbnailUrl,
                        infoLink = it.infoLink,
                    )
                },
        )
    }
}
