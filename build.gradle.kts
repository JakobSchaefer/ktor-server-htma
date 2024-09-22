plugins {
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.kotlin.serialization) apply false
  alias(libs.plugins.node) apply false
  alias(libs.plugins.ktor) apply false
  alias(libs.plugins.spotless)
  alias(libs.plugins.axios)
}

scmVersion {
  snapshotCreator { _, _ ->
    val githubRunNumber = System.getenv("GITHUB_RUN_NUMBER")
    val snapshotSuffix =
        if (githubRunNumber != null && githubRunNumber.isNotBlank()) {
          ".$githubRunNumber"
        } else {
          ""
        }
    "-SNAPSHOT$snapshotSuffix"
  }
}

group = "de.jakobschaefer"

val currentVersion: String = scmVersion.version

tasks.wrapper { distributionType = Wrapper.DistributionType.ALL }

allprojects {
  project.version = currentVersion
  repositories { mavenCentral() }
}

spotless {
  kotlin { ktfmt().configure { target("*/src/**/*.kt", "*/*.kts") } }

  format("web") {
    target("*/web/**/*.js", "*/web/**/*.css", "*/web/**/*.html")

    prettier()
  }
}

tasks.create("writeCurrentVersion") {
  outputs.file("version.txt")
  inputs.property("version", project.version)
  doLast { project.file("version.txt").writeText(project.version as String, Charsets.UTF_8) }
}

tasks.create("format") { dependsOn("spotlessApply") }
