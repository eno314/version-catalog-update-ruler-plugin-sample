package jp.eno314.vcu.pdate.ruler.sample.service

import jakarta.validation.Valid
import jp.eno314.vcu.pdate.ruler.sample.repository.RssDto
import jp.eno314.vcu.pdate.ruler.sample.repository.RssItemDto
import jp.eno314.vcu.pdate.ruler.sample.repository.RssRepository
import jp.eno314.vcu.pdate.ruler.sample.repository.SiteInfoDto
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated
import java.net.URI

@Service
@Validated
class RssService(
    private val rssRepository: RssRepository,
) {
    fun fetchRss(
        @Valid request: RssFetchRequest,
    ): RssFetchResponse {
        val uri = URI.create(request.rssUrl!!)
        val rssDto = rssRepository.fetchRss(uri)
        return rssDto.toResponse()
    }

    private fun RssDto.toResponse(): RssFetchResponse =
        RssFetchResponse(
            siteInfo = siteInfo.toResponse(),
            items = items.map { it.toResponse() },
        )

    private fun SiteInfoDto.toResponse(): SiteInfo =
        SiteInfo(
            title = title,
            link = link,
            description = description,
        )

    private fun RssItemDto.toResponse(): RssItem =
        RssItem(
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
