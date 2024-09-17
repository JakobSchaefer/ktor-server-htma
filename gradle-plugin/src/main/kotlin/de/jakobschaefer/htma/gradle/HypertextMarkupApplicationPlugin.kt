package de.jakobschaefer.htma.gradle

import com.github.gradle.node.NodeExtension
import com.github.gradle.node.NodePlugin
import com.github.gradle.node.npm.task.NpxTask
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors
import kotlin.io.path.pathString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources

class HypertextMarkupApplicationPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.plugins.apply(NodePlugin::class.java)

    project.configure<NodeExtension> {
      download.set(true)
      version.set("20.16.0")
    }

    project.tasks.create("npxViteBuild", NpxTask::class.java) {
      dependsOn("npmInstall")
      description = "Executes npx vite build"
      command.set("vite")
      args.set(listOf("build"))
      inputs.files("package.json", "package-lock.json", "vite.config.js")
      inputs.dir("web")
      inputs.dir(project.fileTree("node_modules").exclude(".cache"))
      outputs.dir("dist")
    }
    project.tasks.named("build") { dependsOn("npxViteBuild") }

    project.tasks.named("clean", Delete::class.java) { delete("dist") }

    project.tasks.create("buildAppManifest") {
      val outputDir = project.layout.buildDirectory.dir("app-server")
      inputs.dir("web")
      outputs.dir(outputDir)
      doLast {
        val webPath = Paths.get(project.projectDir.path, "web")
        val appManifestPages =
            Files.walk(Paths.get(project.projectDir.path, "web"))
                .filter { it.pathString.endsWith(".html") }
                .map {
                  val base =
                      it.pathString.substringAfter(webPath.pathString).substringBeforeLast(".html")
                  AppManifestPage(
                      remotePath =
                          if (base == "/index") {
                            "/"
                          } else if (base.endsWith("index")) {
                            base.substringBeforeLast("/index")
                          } else {
                            base
                          },
                      templateName = base.substringAfter("/"))
                }
                .collect(Collectors.toList())
        val webStructure =
            AppManifest(
                pages = appManifestPages,
            )
        val appManifest = Json { prettyPrint = true }.encodeToString(webStructure)
        outputDir.get().file("manifest.json").asFile.writeText(appManifest)
      }
    }

    project.tasks.withType<ProcessResources>().configureEach {
      from(project.tasks.named("npxViteBuild")) { into("WEB-INF") }
      from(project.tasks.named("buildAppManifest")) { into("WEB-INF/.app") }
      from("web") {
        include("**/*.html")
        include("**/*.properties")
        into("WEB-INF/web")
      }
    }
  }
}
