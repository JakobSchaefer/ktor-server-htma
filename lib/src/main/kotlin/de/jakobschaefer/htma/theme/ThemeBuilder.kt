package de.jakobschaefer.htma.theme

import io.ktor.server.application.*

interface ThemeBuilder {
  suspend fun buildTheme(call: ApplicationCall): Theme
}
