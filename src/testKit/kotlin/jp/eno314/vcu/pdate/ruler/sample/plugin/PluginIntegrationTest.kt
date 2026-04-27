package jp.eno314.vcu.pdate.ruler.sample.plugin

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class PluginIntegrationTest {
    @TempDir
    lateinit var testProjectDir: File

    @Test
    fun `versionCatalogUpdate task runs successfully`() {
        // Setup test project
        File(testProjectDir, "settings.gradle.kts").writeText(
            """
            rootProject.name = "test-project"
            """.trimIndent(),
        )

        File(testProjectDir, "build.gradle.kts").writeText(
            """
            plugins {
                id("io.github.eno314.version-catalog-update-ruler") version "0.0.3"
            }

            repositories {
                mavenCentral()
            }
            """.trimIndent(),
        )

        File(testProjectDir, "gradle").mkdir()
        File(testProjectDir, "gradle/libs.versions.toml").writeText(
            """
            [versions]
            spring-boot = "3.4.1"

            [libraries]
            spring-boot-starter = { module = "org.springframework.boot:spring-boot-starter", version.ref = "spring-boot" }
            """.trimIndent(),
        )

        // Run the task
        val result =
            GradleRunner
                .create()
                .withProjectDir(testProjectDir)
                .withArguments("versionCatalogUpdate")
                .build()

        // Verify the outcome
        assertEquals(TaskOutcome.SUCCESS, result.task(":versionCatalogUpdate")?.outcome)
        assertTrue(result.output.contains("BUILD SUCCESSFUL"))
    }
}
