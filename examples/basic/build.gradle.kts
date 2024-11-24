plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.ktor)
  id("de.jakobschaefer.htma")
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(project(":htma-ktor-server"))
  implementation(libs.ktor.server.netty)

  implementation(libs.slf4j)
  runtimeOnly(libs.log4j.core)
  runtimeOnly(libs.log4j.slf4j)
}
