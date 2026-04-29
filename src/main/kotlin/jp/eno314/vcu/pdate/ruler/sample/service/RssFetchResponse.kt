package jp.eno314.vcu.pdate.ruler.sample.service

import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

data class RssFetchResponse(
    @field:Schema(description = "Information about the RSS site")
    val siteInfo: SiteInfo,
    @field:Schema(description = "List of RSS items")
    val items: List<RssItem>,
)

data class SiteInfo(
    @field:Schema(description = "Title of the site", example = "Example Site")
    val title: String,
    @field:Schema(description = "Link to the site", example = "https://example.com")
    val link: String,
    @field:Schema(description = "Description of the site", example = "An example site for RSS")
    val description: String?,
)

data class RssItem(
    @field:Schema(description = "Unique ID of the item", example = "12345")
    val id: String?,
    @field:Schema(description = "Title of the item", example = "Example Article")
    val title: String,
    @field:Schema(description = "Link to the item", example = "https://example.com/articles/1")
    val link: String,
    @field:Schema(description = "Summary of the item in HTML format", example = "<p>This is an example summary.</p>")
    val summaryHtml: String?,
    @field:Schema(description = "Publication date and time of the item")
    val publishedAt: OffsetDateTime,
    @field:Schema(description = "Author of the item", example = "John Doe")
    val author: String?,
    @field:Schema(description = "URL of the thumbnail image", example = "https://example.com/thumb.jpg")
    val thumbnailUrl: String?,
    @field:Schema(description = "Categories associated with the item", example = "[\"Technology\", \"Programming\"]")
    val categories: List<String>,
)
