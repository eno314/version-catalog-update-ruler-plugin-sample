package jp.eno314.vcu.pdate.ruler.sample.infrastructure

import org.springframework.stereotype.Component

@Component
class RssClient {
    @Suppress("UnusedParameter")
    fun fetch(request: RssFetchRemoteRequest): RssFetchRemoteResponse {
        // ダミーのXML文字列を返す実装
        val dummyXml =
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss version="2.0">
                <channel>
                    <title>サイトのタイトル</title>
                    <link>https://example.com</link>
                    <description>サイトの概要説明</description>
                    <item>
                        <guid>https://example.com/article/123</guid>
                        <title>記事のタイトル</title>
                        <link>https://example.com/article/123</link>
                        <description><![CDATA[<p>ヘッドラインのテキストやHTML...</p>]]></description>
                        <pubDate>2026-04-25T10:00:00Z</pubDate>
                        <author>著者名</author>
                        <category>テクノロジー</category>
                        <category>プログラミング</category>
                    </item>
                </channel>
            </rss>
            """.trimIndent()

        return RssFetchRemoteResponse(rawXml = dummyXml)
    }
}
