package de.jakobschaefer.htma

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.util.*

class HypertextMarkupApplicationTests :
    FunSpec({
      test("basic app server setup") {
        testApplication {
          serverConfig { developmentMode = false }
          install(HypertextMarkupApplication)

          routing {
            // Active web routing...
            web {
              page("/") {
                // Add data to the page context
                data { put("name", "World") }
              }
            }
          }

          val response = client.get("/")
          response.status shouldBe HttpStatusCode.OK
          response.bodyAsText() shouldContain "<title>Simple Test Page</title>"
          response.bodyAsText() shouldContain "<h1>Hello World!</h1>"
        }
      }

      test("app server reads messages correctly") {
        testApplication {
          serverConfig { developmentMode = false }
          install(HypertextMarkupApplication) {
            defaultLocale = Locale.ENGLISH
            supportedLocales = listOf(Locale.ENGLISH, Locale.GERMAN)
          }

          routing {
            // Active web routing...
            web {
              page("/") {
                // Add data to the page context
                data { put("name", "Peter") }
              }
            }
          }

          val response = client.get("/") { headers { append("Accept-Language", "de-DE") } }
          response.bodyAsText() shouldContain "<h1>Hallo Peter!</h1>"
        }
      }

      test("assets are linked correctly in prod mode") {
        testApplication {
          serverConfig { developmentMode = false }
          install(HypertextMarkupApplication)

          routing {
            // Active web routing...
            web {}
          }

          val response = client.get("/image")
          response.bodyAsText() shouldContain "src=\"/assets/test-image-abcdef.png\""
        }
      }
    })
