plugins {
    jacoco
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.version.catalog.update.ruler)
}

group = "jp.eno314"
version = "0.0.2-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

sourceSets {
    create("testKit") {
        compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
    }
}

val testKitImplementation by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

dependencies {
    "testKitImplementation"(gradleTestKit())
}

val testKit by tasks.registering(Test::class) {
    description = "Runs the functional tests."
    group = "verification"
    testClassesDirs = sourceSets["testKit"].output.classesDirs
    classpath = sourceSets["testKit"].runtimeClasspath
    mustRunAfter(tasks.test)
    useJUnitPlatform()
}

dependencies {
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.kotlin.reflect)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

detekt {
    source.setFrom(files("src/main/kotlin"))
}

versionCatalogUpdateRuler {
    onlyStable.set(true)
    pinMajorVersion.set(true)
    pinMinorVersion.set(true)
    onlyArtifactVersion.set(true)
}
