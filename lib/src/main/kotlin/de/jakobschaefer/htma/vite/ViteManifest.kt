package de.jakobschaefer.htma.vite

import de.jakobschaefer.htma.loadResource
import java.io.InputStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

typealias ViteManifest = Map<String, ViteChunk>

fun loadViteManifest(): ViteManifest {
  val manifestJson = loadResource("/WEB-INF/.vite/manifest.json")
  return parseViteManifest(manifestJson)
}

@OptIn(ExperimentalSerializationApi::class)
private fun parseViteManifest(stream: InputStream): ViteManifest {
  val parser = Json { ignoreUnknownKeys = true }
  val manifest = parser.decodeFromStream<ViteManifest>(stream)
  return manifest
}
