package de.jakobschaefer.htma

import java.io.InputStream

internal fun loadResource(path: String): InputStream {
  return object {}.javaClass.getResourceAsStream(path)!!
}
