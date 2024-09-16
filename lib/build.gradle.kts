import java.net.URI

plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.serialization)
  `maven-publish`
}

repositories { mavenCentral() }

dependencies {
  implementation(libs.ktor.server.core)
  testImplementation(libs.ktor.server.test.host)

  implementation(libs.thymeleaf)
  implementation(libs.kotlinx.serialization.json)

  // https://github.com/msasikanth/material-color-utilities-kmm
  implementation("dev.sasikanth:material-color-utilities:1.0.0-alpha01")

  implementation(libs.slf4j)
  testImplementation(libs.log4j.api)
  testImplementation(libs.log4j.core)
  testImplementation(libs.log4j.slf4j)

  testImplementation(libs.kotest.runner.junit5)
  testImplementation(libs.kotest.assertions.core)
}

java {
  withJavadocJar()
  withSourcesJar()
  toolchain { languageVersion = JavaLanguageVersion.of(21) }
}

tasks.withType<Test>().configureEach { useJUnitPlatform() }

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = rootProject.group.toString()
      artifactId = rootProject.name
      version = rootProject.version.toString()
      from(components["java"])

      pom { name = "ktor-server-htma" }
    }
  }
  repositories {
    maven {
      name = "GitHubPackages"
      url = URI.create("https://maven.pkg.github.com/JakobSchaefer/ktor-server-htma")
      credentials {
        username = System.getenv("GITHUB_ACTOR")
        password = System.getenv("GITHUB_TOKEN")
      }
    }
  }
}
