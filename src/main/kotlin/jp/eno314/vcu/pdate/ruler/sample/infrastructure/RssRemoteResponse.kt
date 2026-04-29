package jp.eno314.vcu.pdate.ruler.sample.infrastructure

import java.time.OffsetDateTime

data class RssRemoteResponse(
    val siteInfo: SiteInfoRemoteResponse,
    val items: List<RssItemRemoteResponse>,
)

data class SiteInfoRemoteResponse(
    val title: String,
    val link: String,
    val description: String,
)

data class RssItemRemoteResponse(
    val id: String,
    val title: String,
    val link: String,
    val summaryHtml: String,
    val publishedAt: OffsetDateTime,
    val author: String,
    val thumbnailUrl: String?,
    val categories: List<String>,
)
