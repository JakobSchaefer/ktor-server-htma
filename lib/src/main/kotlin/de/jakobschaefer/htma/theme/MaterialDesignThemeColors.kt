package de.jakobschaefer.htma.theme

import dev.sasikanth.material.color.utilities.dynamiccolor.MaterialDynamicColors
import dev.sasikanth.material.color.utilities.hct.Hct
import dev.sasikanth.material.color.utilities.scheme.*
import dev.sasikanth.material.color.utilities.utils.ColorUtils

enum class SchemeType {
  Content,
  Expressive,
  Fidelity,
  Monochrome,
  Neutral,
  TonalSpot,
  Vibrant
}

internal class MaterialDesignThemeColors(
    val sourceColor: Int,
    val schemeType: SchemeType,
) : ThemeColors {

  override val rootCss: String
    get() {
      val lightScheme = buildSchema(isDark = false, contrast = 0.0)
      val darkScheme = buildSchema(isDark = true, contrast = 0.0)
      return """
            |:root {
            |    ${cssVariables(lightScheme)}
            |}
            |
            |@media (prefers-color-scheme: dark) {
            |  :root {
            |    ${cssVariables(darkScheme)}
            |  }
            |}
            |
            |.elevation-1 {
            |  --md-elevation-level: 1;
            |}
            |.elevation-2 {
            |  --md-elevation-level: 2;
            |}
            |.elevation-3 {
            |  --md-elevation-level: 3;
            |}
            |.elevation-4 {
            |  --md-elevation-level: 4;
            |}
            |.elevation-5 {
            |  --md-elevation-level: 5;
            |}
        """
          .trimMargin()
    }

  private fun buildSchema(isDark: Boolean, contrast: Double): DynamicScheme {
    val hctSourceColor = Hct.fromInt(sourceColor)
    return when (schemeType) {
      SchemeType.Content -> SchemeContent(hctSourceColor, isDark, contrast)
      SchemeType.Expressive -> SchemeExpressive(hctSourceColor, isDark, contrast)
      SchemeType.Fidelity -> SchemeFidelity(hctSourceColor, isDark, contrast)
      SchemeType.Monochrome -> SchemeMonochrome(hctSourceColor, isDark, contrast)
      SchemeType.Neutral -> SchemeNeutral(hctSourceColor, isDark, contrast)
      SchemeType.TonalSpot -> SchemeTonalSpot(hctSourceColor, isDark, contrast)
      SchemeType.Vibrant -> SchemeVibrant(hctSourceColor, isDark, contrast)
    }
  }

  private fun cssVariables(scheme: DynamicScheme): String {
    val colors = MaterialDynamicColors()
    val vars = buildMap {
      putCssColorVariable("primary", colors.primary().getArgb(scheme))
      putCssColorVariable("on-primary", colors.onPrimary().getArgb(scheme))
      putCssColorVariable("primary-container", colors.primaryContainer().getArgb(scheme))
      putCssColorVariable("on-primary-container", colors.onPrimaryContainer().getArgb(scheme))

      putCssColorVariable("secondary", colors.secondary().getArgb(scheme))
      putCssColorVariable("on-secondary", colors.onSecondary().getArgb(scheme))
      putCssColorVariable("secondary-container", colors.secondaryContainer().getArgb(scheme))
      putCssColorVariable("on-secondary-container", colors.onSecondaryContainer().getArgb(scheme))

      putCssColorVariable("tertiary", colors.tertiary().getArgb(scheme))
      putCssColorVariable("on-tertiary", colors.onTertiary().getArgb(scheme))
      putCssColorVariable("tertiary-container", colors.tertiaryContainer().getArgb(scheme))
      putCssColorVariable("on-tertiary-container", colors.onTertiaryContainer().getArgb(scheme))

      putCssColorVariable("error", colors.error().getArgb(scheme))
      putCssColorVariable("on-error", colors.onError().getArgb(scheme))
      putCssColorVariable("error-container", colors.errorContainer().getArgb(scheme))
      putCssColorVariable("on-error-container", colors.onErrorContainer().getArgb(scheme))

      putCssColorVariable("surface", colors.surface().getArgb(scheme))
      putCssColorVariable("on-surface", colors.onSurface().getArgb(scheme))
      putCssColorVariable("on-surface-variant", colors.onSurfaceVariant().getArgb(scheme))

      putCssColorVariable(
          "surface-container-lowest", colors.surfaceContainerLowest().getArgb(scheme))
      putCssColorVariable("surface-container-low", colors.surfaceContainerLow().getArgb(scheme))
      putCssColorVariable("surface-container", colors.surfaceContainer().getArgb(scheme))
      putCssColorVariable("surface-container-high", colors.surfaceContainerHigh().getArgb(scheme))
      putCssColorVariable(
          "surface-container-highest", colors.surfaceContainerHighest().getArgb(scheme))

      putCssColorVariable("inverse-surface", colors.inverseSurface().getArgb(scheme))
      putCssColorVariable("inverse-on-surface", colors.inverseOnSurface().getArgb(scheme))
      putCssColorVariable("inverse-primary", colors.inversePrimary().getArgb(scheme))

      putCssColorVariable("outline", colors.outline().getArgb(scheme))
      putCssColorVariable("outline-variant", colors.outlineVariant().getArgb(scheme))
    }
    return vars.map { "${it.key}: ${it.value};" }.joinToString(" ")
  }

  private fun MutableMap<String, String>.putCssColorVariable(variableName: String, argbColor: Int) {
    val red = ColorUtils.redFromArgb(argbColor)
    val green = ColorUtils.greenFromArgb(argbColor)
    val blue = ColorUtils.blueFromArgb(argbColor)
    put("--color-$variableName", "$red $green $blue")
    put("--md-sys-color-$variableName", "rgb($red, $green, $blue)")
  }
}
