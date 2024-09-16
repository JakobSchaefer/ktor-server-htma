package de.jakobschaefer.htma

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

@Serializable data class AppManifest(val pages: List<AppManifestPage>)

@Serializable data class AppManifestPage(val remotePath: String, val templateName: String)

@OptIn(ExperimentalSerializationApi::class)
fun loadAppManifest(): AppManifest {
  val manifestJson = loadResource("/WEB-INF/.app/manifest.json")
  val appManifest = Json.decodeFromStream<AppManifest>(manifestJson)
  return appManifest
}
