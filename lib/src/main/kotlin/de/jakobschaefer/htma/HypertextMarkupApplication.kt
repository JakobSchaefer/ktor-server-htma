package de.jakobschaefer.htma

import de.jakobschaefer.htma.theme.DefaultThemeBuilder
import de.jakobschaefer.htma.theme.SchemeType
import de.jakobschaefer.htma.theme.ThemeBuilder
import de.jakobschaefer.htma.vite.Vite
import de.jakobschaefer.htma.vite.ViteLinkBuilder
import de.jakobschaefer.htma.vite.loadViteManifest
import de.jakobschaefer.htma.vite.startViteDevServerInBackground
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.response.*
import io.ktor.util.*
import java.util.*
import org.slf4j.LoggerFactory
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.messageresolver.StandardMessageResolver
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.thymeleaf.templateresolver.FileTemplateResolver

internal const val WEB_BASE_FOLDER = "web"

private val log = LoggerFactory.getLogger("AppServer")

class HypertextMarkupApplicationConfig {
  var themeBuilder: ThemeBuilder = DefaultThemeBuilder(0x00ff0000, SchemeType.TonalSpot)
  var defaultLocale: Locale = Locale.getDefault()
  var supportedLocales: List<Locale> = emptyList()
}

val HypertextMarkupApplication =
    createApplicationPlugin(
        "HypertextMarkupApplication", createConfiguration = ::HypertextMarkupApplicationConfig) {
          log.info("Starting app server with default locale ${pluginConfig.defaultLocale}")
          val vite =
              if (application.developmentMode) {
                startViteDevServerInBackground()
              } else {
                val viteManifest = loadViteManifest()
                val entryChunk = viteManifest.values.find { it.isEntry }!!
                Vite(
                    isDevMode = false,
                    js = listOf("/${entryChunk.file}"),
                    css = entryChunk.css.map { "/$it" },
                    assets =
                        viteManifest
                            .filterKeys { key -> key.startsWith(WEB_BASE_FOLDER) }
                            .mapValues { entry -> entry.value.file })
              }

          val templateEngine = TemplateEngine()
          templateEngine.setMessageResolver(StandardMessageResolver())
          templateEngine.setLinkBuilder(ViteLinkBuilder())
          templateEngine.templateResolvers = buildSet {
            if (application.developmentMode) {
              add(
                  FileTemplateResolver().apply {
                    prefix = "${WEB_BASE_FOLDER}/"
                    suffix = ".html"
                    templateMode = TemplateMode.HTML
                    isCacheable = false
                  })
            } else {
              add(
                  ClassLoaderTemplateResolver().apply {
                    prefix = "WEB-INF/${WEB_BASE_FOLDER}/"
                    suffix = ".html"
                    templateMode = TemplateMode.HTML
                  })
            }
          }

          application.useAppServerConfig(pluginConfig)

          on(CallSetup) { call ->
            call.useVite(vite)
            call.useTemplateEngine(templateEngine)
          }
        }

suspend fun ApplicationCall.respondTemplate(templateName: String, data: Map<String, Any>) {
  respondText(contentType = ContentType.Text.Html, status = HttpStatusCode.OK) {
    val acceptLanguageHeader = request.headers["Accept-Language"]
    val locale =
        if (acceptLanguageHeader != null) {
          val acceptedLanguages = Locale.LanguageRange.parse(acceptLanguageHeader)
          Locale.lookup(acceptedLanguages, application.htmaConfig.supportedLocales)
              ?: application.htmaConfig.defaultLocale
        } else {
          application.htmaConfig.defaultLocale
        }
    val ctx = Context(locale, data)
    ctx.setVariable("vite", vite)
    ctx.setVariable("theme", application.htmaConfig.themeBuilder.buildTheme(this))
    templateEngine.process(templateName, ctx)
  }
}

private val viteKey = AttributeKey<Vite>("viteApp")

private val ApplicationCall.vite: Vite
  get() = attributes[viteKey]

private fun ApplicationCall.useVite(vite: Vite) {
  attributes.put(viteKey, vite)
}

private val templateEngineKey = AttributeKey<TemplateEngine>("thymeleaf")

private fun ApplicationCall.useTemplateEngine(templateEngine: TemplateEngine) {
  attributes.put(templateEngineKey, templateEngine)
}

private val ApplicationCall.templateEngine: TemplateEngine
  get() = attributes[templateEngineKey]

private val htmaConfigKey = AttributeKey<HypertextMarkupApplicationConfig>("htmaConfig")

private fun Application.useAppServerConfig(config: HypertextMarkupApplicationConfig) {
  attributes.put(htmaConfigKey, config)
}

private val Application.htmaConfig: HypertextMarkupApplicationConfig
  get() = attributes[htmaConfigKey]
