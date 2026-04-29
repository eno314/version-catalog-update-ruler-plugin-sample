package jp.eno314.vcu.pdate.ruler.sample.service

import jakarta.validation.Valid
import jp.eno314.vcu.pdate.ruler.sample.repository.AtomFetchDto
import jp.eno314.vcu.pdate.ruler.sample.repository.Rss20FetchDto
import jp.eno314.vcu.pdate.ruler.sample.repository.RssRepository
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
        val rssUrlString = requireNotNull(request.rssUrl) { "rssUrl must not be null" }
        val uri = URI.create(rssUrlString)

        return when (val dto = rssRepository.fetchRss(uri)) {
            is Rss20FetchDto ->
                RssFetchResponse(
                    siteInfo =
                        SiteInfo(
                            title = dto.channel.title,
                            link = dto.channel.link,
                            description = dto.channel.description,
                        ),
                    items =
                        dto.items.map {
                            RssItem(
                                id = it.guid,
                                title = it.title,
                                link = it.link,
                                summaryHtml = it.description,
                                publishedAt = it.pubDate,
                                author = it.author ?: "",
                                thumbnailUrl = it.thumbnailUrl,
                                categories = it.categories,
                            )
                        },
                )
            is AtomFetchDto ->
                RssFetchResponse(
                    siteInfo =
                        SiteInfo(
                            title = dto.feed.title,
                            link = dto.feed.link,
                            description = dto.feed.subtitle,
                        ),
                    items =
                        dto.entries.map {
                            RssItem(
                                id = it.id,
                                title = it.title,
                                link = it.link,
                                summaryHtml = it.summary,
                                publishedAt = it.published,
                                author = it.author,
                                thumbnailUrl = it.thumbnailUrl,
                                categories = it.categories,
                            )
                        },
                )
        }
    }
}
