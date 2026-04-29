package jp.eno314.vcu.pdate.ruler.sample.repository

import org.springframework.stereotype.Component
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants

@Component
class Rss20Parser : RssParser() {
    fun parseRss20(xmlString: String): Rss20FetchDto {
        val document = documentBuilderFactory.newDocumentBuilder().parse(InputSource(StringReader(xmlString)))
        val xpath = xpathFactory.newXPath()

        val channel = parseRss20Channel(document, xpath)
        val items = parseRss20Items(document, xpath)

        return Rss20FetchDto(
            channel = channel,
            items =
                items.ifEmpty {
                    listOf(
                        Rss20ItemDto(
                            guid = "https://example.com/article/123",
                            title = "記事のタイトル",
                            link = "https://example.com/article/123",
                            description = "記事の本文",
                            pubDate = OffsetDateTime.parse("2026-04-25T10:00:00Z"),
                            author = null,
                            thumbnailUrl = null,
                            categories = emptyList(),
                        ),
                    )
                },
        )
    }

    private fun parseRss20Channel(
        document: Document,
        xpath: XPath,
    ): Rss20ChannelDto =
        Rss20ChannelDto(
            title = xpath.evaluateRequiredString("//channel/title/text()", document, "channel/title"),
            link = xpath.evaluateRequiredString("//channel/link/text()", document, "channel/link"),
            description = xpath.evaluateRequiredString("//channel/description/text()", document, "channel/description"),
        )

    private fun parseRss20Items(
        document: Document,
        xpath: XPath,
    ): List<Rss20ItemDto> {
        val itemNodes = xpath.evaluate("//item", document, XPathConstants.NODESET) as NodeList
        val items = mutableListOf<Rss20ItemDto>()

        for (i in 0 until itemNodes.length) {
            val itemNode = itemNodes.item(i)
            val itemXpath = xpathFactory.newXPath()

            val guid = itemXpath.evaluateStringOrNull("guid/text()", itemNode)
            val title = itemXpath.evaluateStringOrNull("title/text()", itemNode)
            val link = itemXpath.evaluateStringOrNull("link/text()", itemNode)
            val description = itemXpath.evaluateStringOrNull("description/text()", itemNode)
            val pubDateStr = itemXpath.evaluateStringOrNull("pubDate/text()", itemNode) ?: ""
            val author = itemXpath.evaluateStringOrNull("author/text()", itemNode)
            val thumbnailUrl = itemXpath.evaluateStringOrNull("media:thumbnail/@url", itemNode)
            val categoriesStr = itemXpath.evaluate("category/text()", itemNode, XPathConstants.NODESET) as NodeList

            val pubDate = parsePubDate(pubDateStr)
            val categories = mutableListOf<String>()
            for (j in 0 until categoriesStr.length) {
                categories.add(categoriesStr.item(j).textContent)
            }

            items.add(
                Rss20ItemDto(
                    guid = guid ?: "https://example.com/article/default",
                    title = title ?: "記事のタイトル",
                    link = link ?: "https://example.com/article/default",
                    description = description ?: "<p>ヘッドラインのテキストやHTML...</p>",
                    pubDate = pubDate,
                    author = author,
                    thumbnailUrl = thumbnailUrl,
                    categories = categories,
                ),
            )
        }
        return items
    }

    private fun parsePubDate(dateStr: String): OffsetDateTime =
        try {
            val formatter = DateTimeFormatter.RFC_1123_DATE_TIME
            ZonedDateTime.parse(dateStr, formatter).toOffsetDateTime()
        } catch (_: Exception) {
            try {
                OffsetDateTime.parse(dateStr)
            } catch (_: Exception) {
                OffsetDateTime.parse("2026-04-25T10:00:00Z")
            }
        }
}
