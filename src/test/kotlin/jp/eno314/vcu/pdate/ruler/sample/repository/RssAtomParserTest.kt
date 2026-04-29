package jp.eno314.vcu.pdate.ruler.sample.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.OffsetDateTime

class RssAtomParserTest {
    private val rssAtomParser = RssAtomParser()

    @Test
    fun `parseAtom should parse valid Atom XML correctly`() {
        // Arrange
        val atomXml =
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <feed xmlns="http://www.w3.org/2005/Atom">
                <title>Test Feed</title>
                <link rel="alternate" href="https://test.example.com"/>
                <subtitle>Test Feed Description</subtitle>
                <entry>
                    <id>https://test.example.com/article/1</id>
                    <title>Test Entry 1</title>
                    <link href="https://test.example.com/article/1"/>
                    <summary>Test Entry 1 Summary</summary>
                    <published>2026-04-25T10:00:00Z</published>
                    <author>
                        <name>Test Author</name>
                    </author>
                    <category term="テスト"/>
                    <category term="記事"/>
                </entry>
                <entry>
                    <id>https://test.example.com/article/2</id>
                    <title>Test Entry 2</title>
                    <link href="https://test.example.com/article/2"/>
                    <summary>Test Entry 2 Summary</summary>
                    <published>2026-04-26T10:00:00Z</published>
                </entry>
            </feed>
            """.trimIndent()

        // Act
        val result = rssAtomParser.parseAtom(atomXml)

        // Assert
        assertThat(result).isInstanceOf(AtomFetchDto::class.java)
        assertThat(result.feed.title).isEqualTo("Test Feed")
        assertThat(result.feed.link).isEqualTo("https://test.example.com")
        assertThat(result.feed.subtitle).isEqualTo("Test Feed Description")
        assertThat(result.entries).hasSize(2)

        // First entry assertions
        assertThat(result.entries[0].id).isEqualTo("https://test.example.com/article/1")
        assertThat(result.entries[0].title).isEqualTo("Test Entry 1")
        assertThat(result.entries[0].link).isEqualTo("https://test.example.com/article/1")
        assertThat(result.entries[0].summary).isEqualTo("Test Entry 1 Summary")
        assertThat(result.entries[0].author).isEqualTo("Test Author")
        assertThat(result.entries[0].categories).containsExactly("テスト", "記事")

        // Second entry assertions
        assertThat(result.entries[1].id).isEqualTo("https://test.example.com/article/2")
        assertThat(result.entries[1].title).isEqualTo("Test Entry 2")
        assertThat(result.entries[1].author).isEqualTo("著者名") // Default value when author is missing
    }

    @Test
    fun `parseAtom should use default values for empty elements`() {
        // Arrange
        val atomXml =
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <feed xmlns="http://www.w3.org/2005/Atom">
                <title></title>
                <link/>
                <subtitle></subtitle>
            </feed>
            """.trimIndent()

        // Act & Assert
        val exception =
            assertThrows<IllegalArgumentException> {
                rssAtomParser.parseAtom(atomXml)
            }
        assertThat(exception.message).isEqualTo("Required element 'feed/link' is missing")
    }

    @Test
    fun `parseAtom should provide dummy entry when no entries exist`() {
        // Arrange
        val atomXml =
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <feed xmlns="http://www.w3.org/2005/Atom">
                <title>Test Feed</title>
                <link href="https://test.example.com"/>
                <subtitle>Test Feed Description</subtitle>
            </feed>
            """.trimIndent()

        // Act
        val result = rssAtomParser.parseAtom(atomXml)

        // Assert
        assertThat(result.entries).hasSize(1)
        assertThat(result.entries[0].id).isEqualTo("https://example.com/article/123")
        assertThat(result.entries[0].title).isEqualTo("記事のタイトル")
    }

    @Test
    fun `parseAtom should handle ISO 8601 date format`() {
        // Arrange
        val atomXml =
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <feed xmlns="http://www.w3.org/2005/Atom">
                <title>Test Feed</title>
                <link href="https://test.example.com"/>
                <subtitle>Test</subtitle>
                <entry>
                    <id>test-1</id>
                    <title>Test</title>
                    <link href="https://test.example.com/1"/>
                    <summary>Test</summary>
                    <published>2026-04-25T10:00:00Z</published>
                </entry>
            </feed>
            """.trimIndent()

        // Act
        val result = rssAtomParser.parseAtom(atomXml)

        // Assert
        assertThat(result.entries[0].published).isNotNull()
        assertThat(result.entries[0].published.year).isEqualTo(2026)
        assertThat(result.entries[0].published.monthValue).isEqualTo(4)
        assertThat(result.entries[0].published.dayOfMonth).isEqualTo(25)
    }

    @Test
    fun `parseAtom should handle invalid date with default fallback`() {
        // Arrange
        val atomXml =
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <feed xmlns="http://www.w3.org/2005/Atom">
                <title>Test Feed</title>
                <link href="https://test.example.com"/>
                <subtitle>Test</subtitle>
                <entry>
                    <id>test-1</id>
                    <title>Test</title>
                    <link href="https://test.example.com/1"/>
                    <summary>Test</summary>
                    <published>invalid-date</published>
                </entry>
            </feed>
            """.trimIndent()

        // Act
        val result = rssAtomParser.parseAtom(atomXml)

        // Assert
        assertThat(result.entries[0].published).isEqualTo(OffsetDateTime.parse("2026-04-25T10:00:00Z"))
    }

    @Test
    fun `parseAtom should throw exception for missing required feed title`() {
        // Arrange
        val atomXml =
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <feed xmlns="http://www.w3.org/2005/Atom">
                <title></title>
                <link href="https://test.example.com"/>
                <subtitle>Test Feed Description</subtitle>
            </feed>
            """.trimIndent()

        // Act & Assert
        val exception =
            assertThrows<IllegalArgumentException> {
                rssAtomParser.parseAtom(atomXml)
            }
        assertThat(exception.message).isEqualTo("Required element 'feed/title' is missing")
    }

    @Test
    fun `parseAtom should throw exception for missing required feed link`() {
        // Arrange
        val atomXml =
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <feed xmlns="http://www.w3.org/2005/Atom">
                <title>Test Feed</title>
                <subtitle>Test Feed Description</subtitle>
            </feed>
            """.trimIndent()

        // Act & Assert
        val exception =
            assertThrows<IllegalArgumentException> {
                rssAtomParser.parseAtom(atomXml)
            }
        assertThat(exception.message).isEqualTo("Required element 'feed/link' is missing")
    }

    @Test
    fun `parseAtom should throw exception for missing required feed subtitle`() {
        // Arrange
        val atomXml =
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <feed xmlns="http://www.w3.org/2005/Atom">
                <title>Test Feed</title>
                <link href="https://test.example.com"/>
                <subtitle></subtitle>
            </feed>
            """.trimIndent()

        // Act & Assert
        val exception =
            assertThrows<IllegalArgumentException> {
                rssAtomParser.parseAtom(atomXml)
            }
        assertThat(exception.message).isEqualTo("Required element 'feed/subtitle' is missing")
    }
}
