package jp.eno314.vcu.pdate.ruler.sample.repository

import java.time.OffsetDateTime

data class RssDto(
    val siteInfo: SiteInfoDto,
    val items: List<RssItemDto>,
)

data class SiteInfoDto(
    val title: String,
    val link: String,
    val description: String,
)

data class RssItemDto(
    val id: String,
    val title: String,
    val link: String,
    val summaryHtml: String,
    val publishedAt: OffsetDateTime,
    val author: String,
    val thumbnailUrl: String?,
    val categories: List<String>,
)
