package de.jakobschaefer.htma.theme

import io.ktor.server.application.*

class DefaultThemeBuilder(val sourceColorAsArgb: Int, val schemeType: SchemeType) : ThemeBuilder {

  override suspend fun buildTheme(call: ApplicationCall): Theme {
    return Theme(colors = MaterialDesignThemeColors(sourceColorAsArgb, schemeType))
  }
}
