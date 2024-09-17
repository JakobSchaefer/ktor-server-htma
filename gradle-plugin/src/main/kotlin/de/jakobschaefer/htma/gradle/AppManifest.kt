package de.jakobschaefer.htma.gradle

import kotlinx.serialization.Serializable

@Serializable data class AppManifest(val pages: List<AppManifestPage>)

@Serializable data class AppManifestPage(val remotePath: String, val templateName: String)
