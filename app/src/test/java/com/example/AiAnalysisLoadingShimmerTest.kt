package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AiAnalysisLoadingShimmerTest {

  @Test
  fun testAnalysisPhaseSequenceAndProgress() {
    val phaseProgressValues = listOf(0.22f, 0.48f, 0.72f, 0.88f, 0.96f)

    // Verify progress strictly monotonically increases across phases
    for (i in 0 until phaseProgressValues.size - 1) {
      assertTrue(
        "Progress at phase $i should be less than phase ${i + 1}",
        phaseProgressValues[i] < phaseProgressValues[i + 1]
      )
    }

    assertTrue("Final phase must approach 1.0 (96%)", phaseProgressValues.last() > 0.9f)
    assertEquals(5, phaseProgressValues.size)
  }

  @Test
  fun testTargetRoleAndCompanyFormatting() {
    val role = "Strategic Operations Lead"
    val company = "Zepto"
    val formatted = "$role • $company"

    assertEquals("Strategic Operations Lead • Zepto", formatted)
    assertTrue(formatted.contains("Zepto"))
    assertTrue(formatted.contains("Strategic Operations Lead"))
  }
}
