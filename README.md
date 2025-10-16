# Version Catalog Update Ruler Sample

[![codecov](https://codecov.io/gh/eno314/version-catalog-update-ruler-plugin-sample/graph/badge.svg?token=SSZJM6GALR)](https://codecov.io/gh/eno314/version-catalog-update-ruler-plugin-sample)

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

## Automated Daily Version Catalog Updates (GitHub Actions)

A scheduled workflow (`.github/workflows/daily-version-catalog-update.yml`) runs once per day and attempts to update
`gradle/libs.versions.toml` using the plugin rules defined above.

Workflow key points:

- Schedule: `cron: '15 1 * * *'` (01:15 UTC daily). Adjust for your timezone if needed.
- Task executed: `./gradlew versionCatalogUpdate` followed by a full `./gradlew build` (compile, lint, tests, coverage)
  to
  ensure the suggested updates are safe.
- Branch naming: `chore/version-catalog-update-YYYYMMDD` (same branch reused per day if rerun).
- Creates / updates a PR only when there are changes limited to `gradle/libs.versions.toml`.
- Conservative update policy enforced by plugin settings (stable patch-level only).
- Manual trigger: you can run it on demand via the GitHub Actions UI (`workflow_dispatch`).
- Concurrency group prevents overlapping runs.

Customization ideas:

| Goal                     | Change                                        |
|--------------------------|-----------------------------------------------|
| Different time           | Edit the `cron` expression (uses UTC).        |
| Allow minor updates      | Set `pinMinorVersion.set(false)`.             |
| Allow major updates      | Set `pinMajorVersion.set(false)`.             |
| Accept pre-releases      | Set `onlyStable.set(false)`.                  |
| Broader file scope       | Remove or expand `add-paths` list.            |
| Branch naming convention | Modify the echo step that sets `BRANCH_NAME`. |

Minimal excerpt of the workflow logic:

```yaml
- run: ./gradlew --no-daemon versionCatalogUpdate
- run: ./gradlew --no-daemon build
- uses: peter-evans/create-pull-request@v6
  with:
    add-paths: |
      gradle/libs.versions.toml
```

If no eligible updates are found, the workflow exits cleanly without creating a PR.

## License

Distributed under the terms of the license found in [`LICENSE`]. See the originating plugin project for its own
licensing details.

## Related Links

- Plugin Repository: https://github.com/eno314/version-catalog-update-ruler-plugin
- littlerobots/version-catalog-update-plugin: https://github.com/littlerobots/version-catalog-update-plugin
- Spring Boot Docs: https://docs.spring.io/spring-boot/
- Gradle Version Catalogs: https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog
