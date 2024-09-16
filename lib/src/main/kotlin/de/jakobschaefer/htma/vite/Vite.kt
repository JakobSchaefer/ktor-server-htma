package de.jakobschaefer.htma.vite

data class Vite(
    val isDevMode: Boolean,
    val js: List<String>,
    val css: List<String>,
    val assets: Map<String, String>
)
