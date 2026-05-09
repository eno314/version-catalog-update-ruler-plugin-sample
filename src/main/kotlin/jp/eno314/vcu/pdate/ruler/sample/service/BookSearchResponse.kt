package jp.eno314.vcu.pdate.ruler.sample.service

import io.swagger.v3.oas.annotations.media.Schema

data class BookSearchResponse(
    @field:Schema(description = "Total number of books found")
    val totalItems: Int,
    @field:Schema(description = "List of books found")
    val books: List<BookItem>,
)

data class BookItem(
    @field:Schema(description = "Unique ID of the book", example = "jtmSmfRjkGsC")
    val id: String,
    @field:Schema(description = "Title of the book", example = "Clean Code")
    val title: String,
    @field:Schema(description = "List of authors", example = "[\"Robert C. Martin\"]")
    val authors: List<String>,
    @field:Schema(description = "Publisher of the book", example = "Prentice Hall")
    val publisher: String?,
    @field:Schema(description = "Published date", example = "2008-08-01")
    val publishedDate: String?,
    @field:Schema(description = "Description of the book")
    val description: String?,
    @field:Schema(description = "URL of the thumbnail image", example = "https://books.google.com/...")
    val thumbnailUrl: String?,
    @field:Schema(description = "URL to the Google Books info page", example = "https://books.google.com/books?id=...")
    val infoLink: String?,
)
