package com.example

import de.jakobschaefer.htma.HypertextMarkupApplication
import de.jakobschaefer.htma.web
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
  install(HypertextMarkupApplication)

  routing {
    // Activate Page Routing
    web {
      // Add data to page context
      page("/") { data { put("name", "World") } }
    }
  }
}
