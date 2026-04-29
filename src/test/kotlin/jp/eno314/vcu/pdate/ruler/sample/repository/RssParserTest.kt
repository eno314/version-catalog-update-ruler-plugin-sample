package jp.eno314.vcu.pdate.ruler.sample.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

class RssParserTest {
    private val rssParser = RssParser()

    @Test
    fun `parseRss20 should parse valid RSS 2_0 XML correctly`() {
        // Arrange
        val rss20Xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss version="2.0">
                <channel>
                    <title>Test Channel</title>
                    <link>https://test.example.com</link>
                    <description>Test Channel Description</description>
                    <item>
                        <guid>https://test.example.com/article/1</guid>
                        <title>Test Article 1</title>
                        <link>https://test.example.com/article/1</link>
                        <description>Test Article 1 Description</description>
                        <pubDate>Sat, 25 Apr 2026 10:00:00 +0000</pubDate>
                        <author>Test Author</author>
                        <category>テスト</category>
                        <category>記事</category>
                    </item>
                    <item>
                        <guid>https://test.example.com/article/2</guid>
                        <title>Test Article 2</title>
                        <link>https://test.example.com/article/2</link>
                        <description>Test Article 2 Description</description>
                        <pubDate>2026-04-25T10:00:00Z</pubDate>
                    </item>
                </channel>
            </rss>
        """.trimIndent()

        // Act
        val result = rssParser.parseRss20(rss20Xml)

        // Assert
        assertThat(result).isInstanceOf(Rss20FetchDto::class.java)
        assertThat(result.channel.title).isEqualTo("Test Channel")
        assertThat(result.channel.link).isEqualTo("https://test.example.com")
        assertThat(result.channel.description).isEqualTo("Test Channel Description")
        assertThat(result.items).hasSize(2)

        // First item assertions
        assertThat(result.items[0].guid).isEqualTo("https://test.example.com/article/1")
        assertThat(result.items[0].title).isEqualTo("Test Article 1")
        assertThat(result.items[0].link).isEqualTo("https://test.example.com/article/1")
        assertThat(result.items[0].description).isEqualTo("Test Article 1 Description")
        assertThat(result.items[0].author).isEqualTo("Test Author")
        assertThat(result.items[0].categories).containsExactly("テスト", "記事")

        // Second item assertions
        assertThat(result.items[1].guid).isEqualTo("https://test.example.com/article/2")
        assertThat(result.items[1].title).isEqualTo("Test Article 2")
        assertThat(result.items[1].author).isNull()
    }

    @Test
    fun `parseRss20 should use default values for empty elements`() {
        // Arrange
        val rss20Xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss version="2.0">
                <channel>
                    <title></title>
                    <link></link>
                    <description></description>
                </channel>
            </rss>
        """.trimIndent()

        // Act
        val result = rssParser.parseRss20(rss20Xml)

        // Assert
        assertThat(result.channel.title).isEqualTo("サイトのタイトル")
        assertThat(result.channel.link).isEqualTo("https://example.com")
        assertThat(result.channel.description).isEqualTo("サイトの概要説明")
        assertThat(result.items).hasSize(1) // Should have dummy item
    }

    @Test
    fun `parseRss20 should provide dummy item when no items exist`() {
        // Arrange
        val rss20Xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss version="2.0">
                <channel>
                    <title>Test Channel</title>
                    <link>https://test.example.com</link>
                    <description>Test Channel Description</description>
                </channel>
            </rss>
        """.trimIndent()

        // Act
        val result = rssParser.parseRss20(rss20Xml)

        // Assert
        assertThat(result.items).hasSize(1)
        assertThat(result.items[0].guid).isEqualTo("https://example.com/article/123")
        assertThat(result.items[0].title).isEqualTo("記事のタイトル")
    }

    @Test
    fun `parseAtom should parse valid Atom XML correctly`() {
        // Arrange
        val atomXml = """
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
        val result = rssParser.parseAtom(atomXml)

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
        val atomXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <feed xmlns="http://www.w3.org/2005/Atom">
                <title></title>
                <link/>
                <subtitle></subtitle>
            </feed>
        """.trimIndent()

        // Act
        val result = rssParser.parseAtom(atomXml)

        // Assert
        assertThat(result.feed.title).isEqualTo("サイトのタイトル")
        assertThat(result.feed.link).isEqualTo("https://example.com")
        assertThat(result.feed.subtitle).isEqualTo("サイトの概要説明")
        assertThat(result.entries).hasSize(1) // Should have dummy entry
    }

    @Test
    fun `parseAtom should provide dummy entry when no entries exist`() {
        // Arrange
        val atomXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <feed xmlns="http://www.w3.org/2005/Atom">
                <title>Test Feed</title>
                <link href="https://test.example.com"/>
                <subtitle>Test Feed Description</subtitle>
            </feed>
        """.trimIndent()

        // Act
        val result = rssParser.parseAtom(atomXml)

        // Assert
        assertThat(result.entries).hasSize(1)
        assertThat(result.entries[0].id).isEqualTo("https://example.com/article/123")
        assertThat(result.entries[0].title).isEqualTo("記事のタイトル")
    }

    @Test
    fun `parseRss20 should handle RFC 2822 date format`() {
        // Arrange
        val rss20Xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss version="2.0">
                <channel>
                    <title>Test Channel</title>
                    <link>https://test.example.com</link>
                    <description>Test</description>
                    <item>
                        <guid>test-1</guid>
                        <title>Test</title>
                        <link>https://test.example.com/1</link>
                        <description>Test</description>
                        <pubDate>Sat, 25 Apr 2026 10:00:00 +0000</pubDate>
                    </item>
                </channel>
            </rss>
        """.trimIndent()

        // Act
        val result = rssParser.parseRss20(rss20Xml)

        // Assert
        assertThat(result.items[0].pubDate).isNotNull()
        assertThat(result.items[0].pubDate.year).isEqualTo(2026)
        assertThat(result.items[0].pubDate.monthValue).isEqualTo(4)
        assertThat(result.items[0].pubDate.dayOfMonth).isEqualTo(25)
    }

    @Test
    fun `parseRss20 should handle ISO 8601 date format fallback`() {
        // Arrange
        val rss20Xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss version="2.0">
                <channel>
                    <title>Test Channel</title>
                    <link>https://test.example.com</link>
                    <description>Test</description>
                    <item>
                        <guid>test-1</guid>
                        <title>Test</title>
                        <link>https://test.example.com/1</link>
                        <description>Test</description>
                        <pubDate>2026-04-25T10:00:00Z</pubDate>
                    </item>
                </channel>
            </rss>
        """.trimIndent()

        // Act
        val result = rssParser.parseRss20(rss20Xml)

        // Assert
        assertThat(result.items[0].pubDate).isNotNull()
        assertThat(result.items[0].pubDate.year).isEqualTo(2026)
        assertThat(result.items[0].pubDate.monthValue).isEqualTo(4)
        assertThat(result.items[0].pubDate.dayOfMonth).isEqualTo(25)
    }

    @Test
    fun `parseRss20 should handle invalid date with default fallback`() {
        // Arrange
        val rss20Xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss version="2.0">
                <channel>
                    <title>Test Channel</title>
                    <link>https://test.example.com</link>
                    <description>Test</description>
                    <item>
                        <guid>test-1</guid>
                        <title>Test</title>
                        <link>https://test.example.com/1</link>
                        <description>Test</description>
                        <pubDate>invalid-date</pubDate>
                    </item>
                </channel>
            </rss>
        """.trimIndent()

        // Act
        val result = rssParser.parseRss20(rss20Xml)

        // Assert
        assertThat(result.items[0].pubDate).isEqualTo(OffsetDateTime.parse("2026-04-25T10:00:00Z"))
    }

    @Test
    fun `parseAtom should handle ISO 8601 date format`() {
        // Arrange
        val atomXml = """
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
        val result = rssParser.parseAtom(atomXml)

        // Assert
        assertThat(result.entries[0].published).isNotNull()
        assertThat(result.entries[0].published.year).isEqualTo(2026)
        assertThat(result.entries[0].published.monthValue).isEqualTo(4)
        assertThat(result.entries[0].published.dayOfMonth).isEqualTo(25)
    }

    @Test
    fun `parseAtom should handle invalid date with default fallback`() {
        // Arrange
        val atomXml = """
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
        val result = rssParser.parseAtom(atomXml)

        // Assert
        assertThat(result.entries[0].published).isEqualTo(OffsetDateTime.parse("2026-04-25T10:00:00Z"))
    }

    @Test
    fun `parseRss20 should handle namespaced RSS 2_0 XML correctly`() {
        // Arrange
        val namespacedRss20Xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss xmlns:webfeeds="http://webfeeds.org/rss/1.0" xmlns:note="https://note.com" xmlns:atom="http://www.w3.org/2005/Atom" xmlns:media="http://search.yahoo.com/mrss/" version="2.0">
                <channel>
                    <title>Namespaced Channel</title>
                    <link>https://namespaced.example.com</link>
                    <description>Namespaced Channel Description</description>
                    <item>
                        <guid>https://namespaced.example.com/article/1</guid>
                        <title>Namespaced Article 1</title>
                        <link>https://namespaced.example.com/article/1</link>
                        <description>Namespaced Article 1 Description</description>
                        <pubDate>Sat, 25 Apr 2026 10:00:00 +0000</pubDate>
                        <author>Namespaced Author</author>
                        <category>Namespaced</category>
                        <category>Article</category>
                    </item>
                </channel>
            </rss>
        """.trimIndent()

        // Act
        val result = rssParser.parseRss20(namespacedRss20Xml)

        // Assert
        assertThat(result).isInstanceOf(Rss20FetchDto::class.java)
        assertThat(result.channel.title).isEqualTo("Namespaced Channel")
        assertThat(result.channel.link).isEqualTo("https://namespaced.example.com")
        assertThat(result.channel.description).isEqualTo("Namespaced Channel Description")
        assertThat(result.items).hasSize(1)

        // First item assertions
        assertThat(result.items[0].guid).isEqualTo("https://namespaced.example.com/article/1")
        assertThat(result.items[0].title).isEqualTo("Namespaced Article 1")
        assertThat(result.items[0].link).isEqualTo("https://namespaced.example.com/article/1")
        assertThat(result.items[0].description).isEqualTo("Namespaced Article 1 Description")
        assertThat(result.items[0].author).isEqualTo("Namespaced Author")
        assertThat(result.items[0].categories).containsExactly("Namespaced", "Article")
    }
}
