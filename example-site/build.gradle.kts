plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.ktor)
  id("de.jakobschaefer.ktor-server-htma") version "1.0.0"
}

application { mainClass.set("de.jakobschaefer.website.MainKt") }

dependencies {
  implementation("de.jakobschaefer:ktor-server-htma:0.1.0-SNAPSHOT.2")
  implementation(libs.ktor.server.core)
  implementation(libs.ktor.server.netty)
  testImplementation(libs.ktor.server.test.host)

  implementation(libs.slf4j)
  implementation(libs.log4j.api)
  implementation(libs.log4j.core)
  implementation(libs.log4j.slf4j)

  testImplementation(libs.kotest.runner.junit5)
  testImplementation(libs.kotest.assertions.core)
}

tasks.withType<Test>().configureEach { useJUnitPlatform() }

repositories { maven { url = uri("https://maven.pkg.github.com/JakobSchaefer/standard-service") } }
