package jp.eno314.vcu.pdate.ruler.sample.repository

data class GoogleBooksSearchDto(
    val totalItems: Int,
    val books: List<BookDto>,
)

data class BookDto(
    val id: String,
    val title: String,
    val authors: List<String>,
    val publisher: String?,
    val publishedDate: String?,
    val description: String?,
    val thumbnailUrl: String?,
    val infoLink: String?,
)
