package jp.eno314.vcu.pdate.ruler.sample.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jp.eno314.vcu.pdate.ruler.sample.service.BookSearchRequest
import jp.eno314.vcu.pdate.ruler.sample.service.BookSearchResponse
import jp.eno314.vcu.pdate.ruler.sample.service.BookSearchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Operations related to book search using Google Books API")
class BookController(
    private val bookSearchService: BookSearchService,
) {
    @GetMapping
    @Operation(
        summary = "Search books by title",
        description = "Searches books using Google Books API by the specified title keyword.",
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the search results")
    @ApiResponse(responseCode = "400", description = "Invalid request (e.g. title is blank)")
    fun searchBooks(
        @Valid request: BookSearchRequest,
    ): BookSearchResponse = bookSearchService.searchBooks(request)
}
