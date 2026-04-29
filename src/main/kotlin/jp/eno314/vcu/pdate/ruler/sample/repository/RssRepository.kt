package jp.eno314.vcu.pdate.ruler.sample.repository

import jp.eno314.vcu.pdate.ruler.sample.infrastructure.RssClient
import jp.eno314.vcu.pdate.ruler.sample.infrastructure.RssItemRemoteResponse
import jp.eno314.vcu.pdate.ruler.sample.infrastructure.RssRemoteResponse
import jp.eno314.vcu.pdate.ruler.sample.infrastructure.SiteInfoRemoteResponse
import org.springframework.stereotype.Repository
import java.net.URI

@Repository
class RssRepository(
    private val rssClient: RssClient,
) {
    fun fetchRss(uri: URI): RssDto {
        val remoteResponse = rssClient.fetch(uri)
        return remoteResponse.toDto()
    }

    private fun RssRemoteResponse.toDto(): RssDto =
        RssDto(
            siteInfo = siteInfo.toDto(),
            items = items.map { it.toDto() },
        )

    private fun SiteInfoRemoteResponse.toDto(): SiteInfoDto =
        SiteInfoDto(
            title = title,
            link = link,
            description = description,
        )

    private fun RssItemRemoteResponse.toDto(): RssItemDto =
        RssItemDto(
            id = id,
            title = title,
            link = link,
            summaryHtml = summaryHtml,
            publishedAt = publishedAt,
            author = author,
            thumbnailUrl = thumbnailUrl,
            categories = categories,
        )
}
