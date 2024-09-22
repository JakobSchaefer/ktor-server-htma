package com.example

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*

class ExampleSiteTests :
    FunSpec({
      test("assert hello message is displayed") {
        testApplication {
          serverConfig { developmentMode = false }
          application { module() }
          val response = client.get("/")
          response.status shouldBe HttpStatusCode.OK
          response.bodyAsText() shouldContain "Hello World"
        }
      }
    })
