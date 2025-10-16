# Version Catalog Update Ruler Sample

This repository is a minimal sample implementation demonstrating the usage of the Gradle plugin:

> https://github.com/eno314/version-catalog-update-ruler-plugin

It is a Kotlin + Spring Boot project that shows how to configure and apply the plugin to manage dependency version
updates in a Gradle Version Catalog (`gradle/libs.versions.toml`).

## Overview

| Item           | Value                                            |
|----------------|--------------------------------------------------|
| Language       | Kotlin (JVM, toolchain Java 21)                  |
| Framework      | Spring Boot 3 (Web starter)                      |
| Build Tool     | Gradle (Kotlin DSL)                              |
| Plugin Focus   | `io.github.eno314.version-catalog-update-ruler`  |
| Quality / Lint | detekt, ktlint                                   |
| Tests          | JUnit 5 (Kotlin test + Spring Boot starter test) |
| Coverage       | JaCoCo report generation                         |

The application itself is intentionally tiny (a single `@SpringBootApplication` class) so the focus stays on dependency
governance via the Version Catalog Update Ruler plugin.

## Version Catalog Update Ruler Plugin Configuration

The `versionCatalogUpdateRuler { ... }` block in `build.gradle.kts` contains:

```kotlin
versionCatalogUpdateRuler {
    onlyStable.set(true)
    pinMajorVersion.set(true)
    pinMinorVersion.set(true)
    onlyArtifactVersion.set(true)
}
```

Meaning:

- `onlyStable = true`: Reject update candidates that are not considered stable (e.g. -RC, -M, -SNAPSHOT).
- `pinMajorVersion = true`: Do not jump to a newer major version automatically.
- `pinMinorVersion = true`: Do not jump to a newer minor version automatically (so only patch upgrades are allowed
  unless manually adjusted).
- `onlyArtifactVersion = true`: Restrict update evaluation to artifacts already declared (avoid expanding the catalog
  unintentionally).

Together these settings enforce conservative, safe upgrades (patch-level for existing artifacts, stable releases only)
while still keeping visibility over available changes.

Run the `./gradlew versionCatalogUpdate` task to see how the plugin filters and suggests updates based on these rules.

## License

Distributed under the terms of the license found in [`LICENSE`]. See the originating plugin project for its own
licensing details.

## Related Links

- Plugin Repository: https://github.com/eno314/version-catalog-update-ruler-plugin
- littlerobots/version-catalog-update-plugin: https://github.com/littlerobots/version-catalog-update-plugin
- Spring Boot Docs: https://docs.spring.io/spring-boot/
- Gradle Version Catalogs: https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog
