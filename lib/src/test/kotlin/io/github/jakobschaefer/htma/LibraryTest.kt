package io.github.jakobschaefer.htma;

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue

class LibraryTest {
  @Test
  fun someLibraryMethodReturnsTrue() {
    val classUnderTest = Library()
    assertTrue(classUnderTest.someLibraryMethod(), "someLibraryMethod should return 'true'")
  }
}
