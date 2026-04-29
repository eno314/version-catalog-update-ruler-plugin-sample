package jp.eno314.vcu.pdate.ruler.sample.repository

import jp.eno314.vcu.pdate.ruler.sample.infrastructure.RssClient
import jp.eno314.vcu.pdate.ruler.sample.infrastructure.RssFetchRemoteRequest
import org.springframework.stereotype.Repository
import java.net.URI
import java.time.OffsetDateTime

@Repository
class RssRepository(
    private val rssClient: RssClient,
) {
    fun fetchRss(uri: URI): RssFetchDto {
        val remoteRequest = RssFetchRemoteRequest(uri = uri)
        val remoteResponse = rssClient.fetch(remoteRequest)

        // ダミーデータからバージョンを解析してDtoを生成する
        return if (remoteResponse.rawXml.contains("<rss version=\"2.0\">")) {
            Rss20FetchDto(
                channel = Rss20ChannelDto(
                    title = "サイトのタイトル",
                    link = "https://example.com",
                    description = "サイトの概要説明",
                ),
                items = listOf(
                    Rss20ItemDto(
                        guid = "https://example.com/article/123",
                        title = "記事のタイトル",
                        link = "https://example.com/article/123",
                        description = "<p>ヘッドラインのテキストやHTML...</p>",
                        pubDate = OffsetDateTime.parse("2026-04-25T10:00:00Z"),
                        author = "著者名",
                        thumbnailUrl = "https://example.com/images/thumb.jpg",
                        categories = listOf("テクノロジー", "プログラミング"),
                    )
                )
            )
        } else if (remoteResponse.rawXml.contains("<feed")) {
            AtomFetchDto(
                feed = AtomFeedDto(
                    title = "サイトのタイトル",
                    link = "https://example.com",
                    subtitle = "サイトの概要説明",
                ),
                entries = listOf(
                    AtomEntryDto(
                        id = "https://example.com/article/123",
                        title = "記事のタイトル",
                        link = "https://example.com/article/123",
                        summary = "<p>ヘッドラインのテキストやHTML...</p>",
                        published = OffsetDateTime.parse("2026-04-25T10:00:00Z"),
                        author = "著者名",
                        thumbnailUrl = "https://example.com/images/thumb.jpg",
                        categories = listOf("テクノロジー", "プログラミング"),
                    )
                )
            )
        } else {
            throw IllegalArgumentException("Unsupported RSS format")
        }
    }
}
