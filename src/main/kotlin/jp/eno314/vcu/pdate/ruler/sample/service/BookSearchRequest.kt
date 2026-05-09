package jp.eno314.vcu.pdate.ruler.sample.service

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class BookSearchRequest(
    @field:NotBlank
    @field:Schema(
        description = "The title keyword to search for books",
        example = "Clean Code",
        required = true,
    )
    val title: String,
    @field:NotBlank
    @field:Schema(
        description = "Google Books API key (required).",
        example = "YOUR_API_KEY",
        required = true,
    )
    val googleBooksApiKey: String,
    @field:Schema(
        description =
            "Author name to filter books (optional). " +
                "When specified, only books by this author are returned.",
        example = "Robert C. Martin",
    )
    val author: String? = null,
    @field:Schema(
        description =
            "Publisher name to filter books (optional). " +
                "When specified, only books from this publisher are returned.",
        example = "Prentice Hall",
    )
    val publisher: String? = null,
    @field:Schema(
        description =
            "Subject keyword to filter books (optional). " +
                "When specified, only books matching this subject are returned.",
        example = "Programming",
    )
    val subject: String? = null,
    @field:Schema(
        description =
            "Restricts the returned results to a specific print or digital type. " +
                "Default is 'all'.",
        example = "all",
        allowableValues = ["all", "books", "magazines"],
    )
    val printType: String = "all",
    @field:Schema(
        description =
            "Restricts the returned results to those with the specified language. " +
                "Default is 'ja'.",
        example = "ja",
    )
    val langRestrict: String = "ja",
)
