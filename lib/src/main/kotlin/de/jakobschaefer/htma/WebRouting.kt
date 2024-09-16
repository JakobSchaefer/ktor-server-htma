package de.jakobschaefer.htma

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("WebRouting")

@KtorDsl
fun Route.web(spec: WebRoutingBuilder.() -> Unit) {
  val appManifest = loadAppManifest()
  val webRouting = WebRoutingBuilder().apply(spec).build()

  staticResources("/assets", "/WEB-INF/assets") {
    cacheControl { listOf(CacheControl.MaxAge(maxAgeSeconds = 31_104_000)) }
    preCompressed(CompressedFileType.GZIP)
  }

  appManifest.pages.forEach { page ->
    log.info("Serving template ${page.templateName} at ${page.remotePath}")
    get(page.remotePath) {
      val webPage = webRouting.pages[page.remotePath]
      val data =
          if (webPage == null) {
            buildMap {
              for (dataLoader in webRouting.commonLoaders) {
                dataLoader(this@get.call)
              }
            }
          } else {
            buildMap {
              for (dataLoader in webRouting.commonLoaders) {
                dataLoader(this@get.call)
              }
              for (dataLoader in webPage.dataLoaders) {
                dataLoader(this@get.call)
              }
            }
          }
      call.respondTemplate(page.templateName, data)
    }
  }
}

@DslMarker annotation class WebRoutingDsl

typealias LoadFn = suspend MutableMap<String, Any>.(ApplicationCall) -> Unit

@WebRoutingDsl
class WebRoutingBuilder {
  private val webPages = mutableMapOf<String, WebPage>()
  private val commonLoaders = mutableListOf<LoadFn>()

  @KtorDsl
  fun page(path: String, spec: WebPageBuilder.() -> Unit) {
    val page = WebPageBuilder().apply(spec).build()
    webPages[path] = page
  }

  @KtorDsl
  fun commonData(loadFn: LoadFn) {
    commonLoaders.add(loadFn)
  }

  internal fun build(): WebRouting {
    return WebRouting(webPages, commonLoaders)
  }
}

data class WebRouting(val pages: Map<String, WebPage>, val commonLoaders: List<LoadFn>)

@WebRoutingDsl
class WebPageBuilder {
  private val dataLoaders = mutableListOf<LoadFn>()

  @KtorDsl
  fun data(loadFn: LoadFn) {
    dataLoaders.add(loadFn)
  }

  internal fun build(): WebPage {
    return WebPage(dataLoaders)
  }
}

data class WebPage(
    val dataLoaders: List<LoadFn>,
)
