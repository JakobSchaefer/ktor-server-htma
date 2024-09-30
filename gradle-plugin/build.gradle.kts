plugins {
  `kotlin-dsl`
  id("com.gradle.plugin-publish") version "1.2.1"
  id("org.jetbrains.kotlin.plugin.serialization") version "2.0.20"
}

group = "de.jakobschaefer"

version = "0.1.2"

repositories {
  mavenCentral()
  gradlePluginPortal()
}

gradlePlugin {
  website = "https://github.com/JakobSchaefer/ktor-server-htma"
  vcsUrl = "https://github.com/JakobSchaefer/ktor-server-htma.git"
  plugins {
    create("ktor-server-htma") {
      id = "de.jakobschaefer.ktor-server-htma"
      displayName = "HTMA - Hypertext Markup Application"
      description = "Plugin support for the HTMA Framework"
      tags = listOf("htma", "ktor")
      implementationClass = "de.jakobschaefer.htma.gradle.HypertextMarkupApplicationPlugin"
      dependencies {
        implementation(
            "com.github.node-gradle.node:com.github.node-gradle.node.gradle.plugin:latest.release")

        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
      }
    }
  }
}
