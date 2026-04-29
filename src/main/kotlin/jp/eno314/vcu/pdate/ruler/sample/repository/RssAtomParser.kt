package jp.eno314.vcu.pdate.ruler.sample.repository

import org.springframework.stereotype.Component
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import java.time.OffsetDateTime
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants

@Component
class RssAtomParser : RssParser() {
    fun parseAtom(xmlString: String): AtomFetchDto {
        val document = parseXml(xmlString)
        val xpath = xpathFactory.newXPath()

        val feed = parseAtomFeed(document, xpath)
        val entries = parseAtomEntries(document, xpath)

        return AtomFetchDto(
            feed = feed,
            entries =
                entries.ifEmpty {
                    listOf(
                        AtomEntryDto(
                            id = "https://example.com/article/123",
                            title = "記事のタイトル",
                            link = "https://example.com/article/123",
                            summary = "記事の概要",
                            published = OffsetDateTime.parse("2026-04-25T10:00:00Z"),
                            author = "著者名",
                            thumbnailUrl = null,
                            categories = emptyList(),
                        ),
                    )
                },
        )
    }

    private fun parseAtomFeed(
        document: Document,
        xpath: XPath,
    ): AtomFeedDto {
        val feedLink =
            xpath.evaluateStringOrNull("//feed/link[@rel='alternate']/@href", document)
                ?: xpath.evaluateStringOrNull("//feed/link/@href", document)

        requireNotNull(feedLink) { "Required element 'feed/link' is missing" }

        return AtomFeedDto(
            title = xpath.evaluateRequiredString("//feed/title/text()", document, "feed/title"),
            link = feedLink,
            subtitle = xpath.evaluateRequiredString("//feed/subtitle/text()", document, "feed/subtitle"),
        )
    }

    private fun parseAtomEntries(
        document: Document,
        xpath: XPath,
    ): List<AtomEntryDto> {
        val entryNodes = xpath.evaluate("//entry", document, XPathConstants.NODESET) as NodeList
        val entries = mutableListOf<AtomEntryDto>()

        for (i in 0 until entryNodes.length) {
            val entryNode = entryNodes.item(i)
            val entryXpath = xpathFactory.newXPath()

            val id = entryXpath.evaluateStringOrNull("id/text()", entryNode)
            val title = entryXpath.evaluateStringOrNull("title/text()", entryNode)
            val link = entryXpath.evaluateStringOrNull("link/@href", entryNode)
            val summary = entryXpath.evaluateStringOrNull("summary/text()", entryNode)
            val publishedStr = entryXpath.evaluateStringOrNull("published/text()", entryNode) ?: ""
            val author = entryXpath.evaluateStringOrNull("author/name/text()", entryNode) ?: "著者名"
            val thumbnailUrl = entryXpath.evaluateStringOrNull("media:thumbnail/@url", entryNode)
            val categoriesStr = entryXpath.evaluate("category/@term", entryNode, XPathConstants.NODESET) as NodeList

            val published = parsePublishedDate(publishedStr)
            val categories = mutableListOf<String>()
            for (j in 0 until categoriesStr.length) {
                categories.add(categoriesStr.item(j).textContent)
            }

            entries.add(
                AtomEntryDto(
                    id = id ?: "https://example.com/article/default",
                    title = title ?: "記事のタイトル",
                    link = link ?: "https://example.com/article/default",
                    summary = summary ?: "<p>ヘッドラインのテキストやHTML...</p>",
                    published = published,
                    author = author,
                    thumbnailUrl = thumbnailUrl,
                    categories = categories,
                ),
            )
        }

        return entries
    }
}
