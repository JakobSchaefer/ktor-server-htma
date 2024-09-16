package de.jakobschaefer.htma.vite

import de.jakobschaefer.htma.WEB_BASE_FOLDER
import org.thymeleaf.context.IExpressionContext
import org.thymeleaf.linkbuilder.StandardLinkBuilder

class ViteLinkBuilder : StandardLinkBuilder() {
  override fun computeContextPath(
      context: IExpressionContext,
      base: String,
      parameters: MutableMap<String, Any>?
  ): String {
    return "/"
  }

  override fun processLink(context: IExpressionContext, link: String): String {
    val vite = context.getVariable("vite") as Vite
    return if (link.startsWith("~") ||
        link.startsWith("/") ||
        link.matches(Regex("^https?://.+"))) {
      link
    } else {
      if (vite.isDevMode) {
        "http://localhost:5173/$WEB_BASE_FOLDER/$link"
      } else {
        val assetFile = vite.assets["$WEB_BASE_FOLDER/$link"]
        if (assetFile != null) {
          "/${assetFile}"
        } else {
          link
        }
      }
    }
  }
}
