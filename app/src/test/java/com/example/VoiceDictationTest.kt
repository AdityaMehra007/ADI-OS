package com.example

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class VoiceDictationTest {

  private fun appendSpokenText(
    existingText: String,
    newText: String
  ): String {
    val cleanNew = newText.trim().replaceFirstChar {
      if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }

    return if (existingText.isBlank()) {
      cleanNew
    } else {
      val needsSpace = !existingText.endsWith(" ") && !existingText.endsWith("\n")
      val separator = if (existingText.endsWith(".") || existingText.endsWith("?") || existingText.endsWith("!")) {
        " "
      } else if (needsSpace) {
        ". "
      } else {
        ""
      }
      existingText + separator + cleanNew
    }
  }

  @Test
  fun testDictateIntoEmptyField() {
    val spoken = "focus on bengaluru quick commerce logistics"
    val result = appendSpokenText("", spoken)
    assertEquals("Focus on bengaluru quick commerce logistics", result)
  }

  @Test
  fun testDictateMultipleSentencesSequentially() {
    val initial = "Focus on bengaluru quick commerce logistics."
    val followUp = "target corporate business analyst roles with high impact"
    val combined = appendSpokenText(initial, followUp)
    assertEquals(
      "Focus on bengaluru quick commerce logistics. Target corporate business analyst roles with high impact",
      combined
    )
  }

  @Test
  fun testDictateWithoutEndingPunctuation() {
    val initial = "Drafting Zepto unit economics analysis"
    val second = "emphasize batch routing algorithm"
    val combined = appendSpokenText(initial, second)
    assertEquals(
      "Drafting Zepto unit economics analysis. Emphasize batch routing algorithm",
      combined
    )
  }
}
