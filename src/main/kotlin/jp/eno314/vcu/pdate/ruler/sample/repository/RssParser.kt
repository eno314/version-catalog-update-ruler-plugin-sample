package jp.eno314.vcu.pdate.ruler.sample.repository

import org.springframework.stereotype.Component
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

@Component
class RssParser {
    private val documentBuilderFactory =
        DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = false // Ignore namespaces for XPath compatibility
        }
    private val xpathFactory = XPathFactory.newInstance()

    fun parseRss20(xmlString: String): Rss20FetchDto {
        val document = parseXml(xmlString)
        val xpath = xpathFactory.newXPath()

        // Parse channel information
        val channelTitle =
            (xpath.evaluate("//channel/title/text()", document, XPathConstants.STRING) as String).takeIf { it.isNotEmpty() } ?: "サイトのタイトル"
        val channelLink =
            (xpath.evaluate("//channel/link/text()", document, XPathConstants.STRING) as String).takeIf { it.isNotEmpty() }
                ?: "https://example.com"
        val channelDescription =
            (xpath.evaluate("//channel/description/text()", document, XPathConstants.STRING) as String).takeIf { it.isNotEmpty() }
                ?: "サイトの概要説明"

        // Parse items
        val itemNodes = xpath.evaluate("//item", document, XPathConstants.NODESET) as NodeList
        val items = mutableListOf<Rss20ItemDto>()

        for (i in 0 until itemNodes.length) {
            val itemNode = itemNodes.item(i)
            val itemXpath = xpathFactory.newXPath()

            val guid = itemXpath.evaluate("guid/text()", itemNode, XPathConstants.STRING) as String
            val title = itemXpath.evaluate("title/text()", itemNode, XPathConstants.STRING) as String
            val link = itemXpath.evaluate("link/text()", itemNode, XPathConstants.STRING) as String
            val description = itemXpath.evaluate("description/text()", itemNode, XPathConstants.STRING) as String
            val pubDateStr = itemXpath.evaluate("pubDate/text()", itemNode, XPathConstants.STRING) as String
            val author = (itemXpath.evaluate("author/text()", itemNode, XPathConstants.STRING) as String).takeIf { it.isNotEmpty() }
            val thumbnailUrl =
                (
                    itemXpath.evaluate(
                        "media:thumbnail/@url",
                        itemNode,
                        XPathConstants.STRING,
                    ) as? String
                )?.takeIf { it.isNotEmpty() }
            val categoriesStr = itemXpath.evaluate("category/text()", itemNode, XPathConstants.NODESET) as NodeList

            val pubDate = parsePubDate(pubDateStr)
            val categories = mutableListOf<String>()
            for (j in 0 until categoriesStr.length) {
                categories.add(categoriesStr.item(j).textContent)
            }

            items.add(
                Rss20ItemDto(
                    guid = guid.takeIf { it.isNotEmpty() } ?: "https://example.com/article/default",
                    title = title.takeIf { it.isNotEmpty() } ?: "記事のタイトル",
                    link = link.takeIf { it.isNotEmpty() } ?: "https://example.com/article/default",
                    description = description.takeIf { it.isNotEmpty() } ?: "<p>ヘッドラインのテキストやHTML...</p>",
                    pubDate = pubDate,
                    author = author,
                    thumbnailUrl = thumbnailUrl,
                    categories = categories,
                ),
            )
        }

        return Rss20FetchDto(
            channel = Rss20ChannelDto(title = channelTitle, link = channelLink, description = channelDescription),
            items =
                items.takeIf { it.isNotEmpty() } ?: listOf(
                    Rss20ItemDto(
                        guid = "https://example.com/article/123",
                        title = "記事のタイトル",
                        link = "https://example.com/article/123",
                        description = "<p>ヘッドラインのテキストやHTML...</p>",
                        pubDate = OffsetDateTime.parse("2026-04-25T10:00:00Z"),
                        author = "著者名",
                        thumbnailUrl = "https://example.com/images/thumb.jpg",
                        categories = listOf("テクノロジー", "プログラミング"),
                    ),
                ),
        )
    }

    fun parseAtom(xmlString: String): AtomFetchDto {
        val document = parseXml(xmlString)
        val xpath = xpathFactory.newXPath()

        // Parse feed information
        val feedTitle =
            (xpath.evaluate("//feed/title/text()", document, XPathConstants.STRING) as String).takeIf { it.isNotEmpty() } ?: "サイトのタイトル"
        val feedLink =
            (xpath.evaluate("//feed/link[@rel='alternate']/@href", document, XPathConstants.STRING) as String).takeIf { it.isNotEmpty() }
                ?: (xpath.evaluate("//feed/link/@href", document, XPathConstants.STRING) as String).takeIf { it.isNotEmpty() }
                ?: "https://example.com"
        val feedSubtitle =
            (xpath.evaluate("//feed/subtitle/text()", document, XPathConstants.STRING) as String).takeIf { it.isNotEmpty() } ?: "サイトの概要説明"

        // Parse entries
        val entryNodes = xpath.evaluate("//entry", document, XPathConstants.NODESET) as NodeList
        val entries = mutableListOf<AtomEntryDto>()

        for (i in 0 until entryNodes.length) {
            val entryNode = entryNodes.item(i)
            val entryXpath = xpathFactory.newXPath()

            val id = entryXpath.evaluate("id/text()", entryNode, XPathConstants.STRING) as String
            val title = entryXpath.evaluate("title/text()", entryNode, XPathConstants.STRING) as String
            val link = entryXpath.evaluate("link/@href", entryNode, XPathConstants.STRING) as String
            val summary = entryXpath.evaluate("summary/text()", entryNode, XPathConstants.STRING) as String
            val publishedStr = entryXpath.evaluate("published/text()", entryNode, XPathConstants.STRING) as String
            val author =
                (entryXpath.evaluate("author/name/text()", entryNode, XPathConstants.STRING) as String).takeIf { it.isNotEmpty() } ?: "著者名"
            val thumbnailUrl =
                (
                    entryXpath.evaluate(
                        "media:thumbnail/@url",
                        entryNode,
                        XPathConstants.STRING,
                    ) as? String
                )?.takeIf { it.isNotEmpty() }
            val categoriesStr = entryXpath.evaluate("category/@term", entryNode, XPathConstants.NODESET) as NodeList

            val published = parsePublishedDate(publishedStr)
            val categories = mutableListOf<String>()
            for (j in 0 until categoriesStr.length) {
                categories.add(categoriesStr.item(j).textContent)
            }

            entries.add(
                AtomEntryDto(
                    id = id.takeIf { it.isNotEmpty() } ?: "https://example.com/article/default",
                    title = title.takeIf { it.isNotEmpty() } ?: "記事のタイトル",
                    link = link.takeIf { it.isNotEmpty() } ?: "https://example.com/article/default",
                    summary = summary.takeIf { it.isNotEmpty() } ?: "<p>ヘッドラインのテキストやHTML...</p>",
                    published = published,
                    author = author,
                    thumbnailUrl = thumbnailUrl,
                    categories = categories,
                ),
            )
        }

        return AtomFetchDto(
            feed = AtomFeedDto(title = feedTitle, link = feedLink, subtitle = feedSubtitle),
            entries =
                entries.takeIf { it.isNotEmpty() } ?: listOf(
                    AtomEntryDto(
                        id = "https://example.com/article/123",
                        title = "記事のタイトル",
                        link = "https://example.com/article/123",
                        summary = "<p>ヘッドラインのテキストやHTML...</p>",
                        published = OffsetDateTime.parse("2026-04-25T10:00:00Z"),
                        author = "著者名",
                        thumbnailUrl = "https://example.com/images/thumb.jpg",
                        categories = listOf("テクノロジー", "プログラミング"),
                    ),
                ),
        )
    }

    private fun parseXml(xmlString: String): Document {
        val builder = documentBuilderFactory.newDocumentBuilder()
        return builder.parse(InputSource(StringReader(xmlString)))
    }

    private fun parsePubDate(dateStr: String): OffsetDateTime =
        try {
            // Try to parse RFC 2822 format (common in RSS feeds)
            val formatter = DateTimeFormatter.RFC_1123_DATE_TIME
            ZonedDateTime.parse(dateStr, formatter).toOffsetDateTime()
        } catch (_: Exception) {
            // Fallback to ISO format
            try {
                OffsetDateTime.parse(dateStr)
            } catch (_: Exception) {
                // Default date if parsing fails
                OffsetDateTime.parse("2026-04-25T10:00:00Z")
            }
        }

    private fun parsePublishedDate(dateStr: String): OffsetDateTime =
        try {
            // Atom feeds typically use ISO 8601 format
            OffsetDateTime.parse(dateStr)
        } catch (_: Exception) {
            // Fallback
            OffsetDateTime.parse("2026-04-25T10:00:00Z")
        }
}
