import org.gradle.jvm.tasks.Jar
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("java-library")
    id("maven-publish")
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.dependency.management)
}

val starterVersion = libs.versions.release.get()

version = starterVersion
java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    api(project(":usecase-pattern"))
    api(libs.micrometer.core)
    implementation(libs.commons.lang3)

    compileOnly(libs.spring.boot.autoconfigure)
    compileOnly(libs.spring.context)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.micrometer.core)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = "ru.vikulinva"
            artifactId = "usecase-pattern-starter"
            version = starterVersion
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/remodov/usecase-pattern")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: providers.gradleProperty("gpr.user").orNull
                password = System.getenv("GITHUB_TOKEN") ?: providers.gradleProperty("gpr.key").orNull
            }
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

tasks.named<Jar>("jar") {
    enabled = true
    archiveClassifier = ""
}

tasks.named<BootJar>("bootJar") {
    enabled = false
}
