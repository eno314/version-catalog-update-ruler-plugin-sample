package jp.eno314.vcu.pdate.ruler.sample.service

import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated
import java.time.OffsetDateTime

@Service
@Validated
class RssService {
    fun fetchRss(@Valid request: RssFetchRequest): RssFetchResponse {
        // Dummy implementation
        return RssFetchResponse(
            siteInfo = SiteInfo(
                title = "サイトのタイトル",
                link = "https://example.com",
                description = "サイトの概要説明"
            ),
            items = listOf(
                RssItem(
                    id = "https://example.com/article/123",
                    title = "記事のタイトル",
                    link = "https://example.com/article/123",
                    summaryHtml = "<p>ヘッドラインのテキストやHTML...</p>",
                    publishedAt = OffsetDateTime.parse("2026-04-25T10:00:00Z"),
                    author = "著者名",
                    thumbnailUrl = "https://example.com/images/thumb.jpg",
                    categories = listOf("テクノロジー", "プログラミング")
                )
            )
        )
    }
}
