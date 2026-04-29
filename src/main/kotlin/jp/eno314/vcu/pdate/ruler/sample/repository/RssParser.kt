package jp.eno314.vcu.pdate.ruler.sample.repository

import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.StringReader
import java.time.OffsetDateTime
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

abstract class RssParser {
    protected val documentBuilderFactory: DocumentBuilderFactory =
        DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = false // Ignore namespaces for XPath compatibility
        }

    protected val xpathFactory: XPathFactory = XPathFactory.newInstance()

    protected fun parseXml(xmlString: String): Document {
        val builder = documentBuilderFactory.newDocumentBuilder()
        return builder.parse(InputSource(StringReader(xmlString)))
    }

    protected fun parsePublishedDate(dateStr: String): OffsetDateTime =
        try {
            OffsetDateTime.parse(dateStr)
        } catch (_: Exception) {
            OffsetDateTime.parse("2026-04-25T10:00:00Z")
        }

    // --- XPath Helper Extensions ---

    protected fun XPath.evaluateRequiredString(
        expression: String,
        context: Any,
        elementLabel: String,
    ): String {
        val result = evaluate(expression, context, XPathConstants.STRING) as String
        require(result.isNotEmpty()) { "Required element '$elementLabel' is missing" }
        return result
    }

    protected fun XPath.evaluateStringOrNull(
        expression: String,
        context: Any,
    ): String? {
        val result = evaluate(expression, context, XPathConstants.STRING) as String
        return result.takeIf { it.isNotEmpty() }
    }
}
