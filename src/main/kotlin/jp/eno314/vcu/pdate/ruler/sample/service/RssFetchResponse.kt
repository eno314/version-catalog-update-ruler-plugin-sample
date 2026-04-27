package jp.eno314.vcu.pdate.ruler.sample.service

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class RssFetchResponse(
    @field:JsonProperty("site_info")
    val siteInfo: SiteInfo,
    val items: List<RssItem>,
)

data class SiteInfo(
    val title: String,
    val link: String,
    val description: String,
)

data class RssItem(
    val id: String,
    val title: String,
    val link: String,
    @field:JsonProperty("summary_html")
    val summaryHtml: String,
    @field:JsonProperty("published_at")
    val publishedAt: OffsetDateTime,
    val author: String,
    @field:JsonProperty("thumbnail_url")
    val thumbnailUrl: String?,
    val categories: List<String>,
)
