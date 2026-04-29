package jp.eno314.vcu.pdate.ruler.sample.service

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

data class RssFetchResponse(
    @field:JsonProperty("site_info")
    @Schema(description = "Information about the RSS site")
    val siteInfo: SiteInfo,
    @Schema(description = "List of RSS items")
    val items: List<RssItem>,
)

data class SiteInfo(
    @Schema(description = "Title of the site", example = "Example Site")
    val title: String,
    @Schema(description = "Link to the site", example = "https://example.com")
    val link: String,
    @Schema(description = "Description of the site", example = "An example site for RSS")
    val description: String?,
)

data class RssItem(
    @Schema(description = "Unique ID of the item", example = "12345")
    val id: String,
    @Schema(description = "Title of the item", example = "Example Article")
    val title: String,
    @Schema(description = "Link to the item", example = "https://example.com/articles/1")
    val link: String,
    @field:JsonProperty("summary_html")
    @Schema(description = "Summary of the item in HTML format", example = "<p>This is an example summary.</p>")
    val summaryHtml: String,
    @field:JsonProperty("published_at")
    @Schema(description = "Publication date and time of the item")
    val publishedAt: OffsetDateTime,
    @Schema(description = "Author of the item", example = "John Doe")
    val author: String,
    @field:JsonProperty("thumbnail_url")
    @Schema(description = "URL of the thumbnail image", example = "https://example.com/thumb.jpg")
    val thumbnailUrl: String?,
    @Schema(description = "Categories associated with the item", example = "[\"Technology\", \"Programming\"]")
    val categories: List<String>,
)
