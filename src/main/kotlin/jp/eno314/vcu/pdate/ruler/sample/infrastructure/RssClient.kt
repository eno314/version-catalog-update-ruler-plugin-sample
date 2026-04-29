package jp.eno314.vcu.pdate.ruler.sample.infrastructure

import org.springframework.stereotype.Component
import java.net.URI
import java.time.OffsetDateTime

@Component
class RssClient {
    @Suppress("UnusedParameter")
    fun fetch(uri: URI): RssRemoteResponse {
        // Dummy implementation returning fixed data
        return RssRemoteResponse(
            siteInfo =
                SiteInfoRemoteResponse(
                    title = "サイトのタイトル",
                    link = "https://example.com",
                    description = "サイトの概要説明",
                ),
            items =
                listOf(
                    RssItemRemoteResponse(
                        id = "https://example.com/article/123",
                        title = "記事のタイトル",
                        link = "https://example.com/article/123",
                        summaryHtml = "<p>ヘッドラインのテキストやHTML...</p>",
                        publishedAt = OffsetDateTime.parse("2026-04-25T10:00:00Z"),
                        author = "著者名",
                        thumbnailUrl = "https://example.com/images/thumb.jpg",
                        categories = listOf("テクノロジー", "プログラミング"),
                    ),
                ),
        )
    }
}
