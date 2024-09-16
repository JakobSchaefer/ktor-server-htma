package de.jakobschaefer.htma.vite

import java.io.File
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
fun startViteDevServerInBackground(): Vite {
  GlobalScope.launch {
    ProcessBuilder("npx", "vite", "dev")
        .directory(File("."))
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectInput(ProcessBuilder.Redirect.INHERIT)
        .start()
  }
  return Vite(
      isDevMode = true,
      js = listOf("http://localhost:5173/@vite/client", "http://localhost:5173/web/main.js"),
      css = emptyList(),
      assets = emptyMap())
}
