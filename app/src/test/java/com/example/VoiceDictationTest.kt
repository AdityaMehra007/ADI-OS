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
    val followUp = "anchor base compensation at 24L with quarterly vesting"
    val combined = appendSpokenText(initial, followUp)
    assertEquals(
      "Focus on bengaluru quick commerce logistics. Anchor base compensation at 24L with quarterly vesting",
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
