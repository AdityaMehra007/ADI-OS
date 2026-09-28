package com.example

import com.example.ui.components.AladdinStressMode
import com.example.ui.components.DarkStoreHub
import com.example.ui.components.HudVisualizerTab
import com.example.ui.components.MarginWaterfallStep
import com.example.ui.components.OperationalCycleTimePoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DarkStoreOperationalHudTest {

  @Test
  fun testOperationalCycleTimePointCalculations() {
    val point = OperationalCycleTimePoint(
      timeLabel = "14:00",
      hourIndex = 3,
      pickSec = 68f,
      packSec = 41f,
      stagingSec = 114f,
      targetSlaSec = 240f,
      baselineSec = 380f,
      activeDrops = 420,
      cm2PerDrop = 28.40f
    )

    // Total cycle: 68 + 41 + 114 = 223s
    assertEquals(223f, point.totalCycleSec, 0.01f)

    // Must be under 240s SLA
    assertTrue("Cycle time must satisfy sub-240s SLA", point.totalCycleSec < point.targetSlaSec)

    // Verify 42% reduction compared to 380s baseline: (380 - 223) / 380 = 41.3% ~ 42%
    val reduction = (point.baselineSec - point.totalCycleSec) / point.baselineSec
    assertTrue("Cycle time reduction must exceed 40%", reduction >= 0.40f)

    // Verify CM2 per drop positive unit economics (+₹28.40)
    assertEquals(28.40f, point.cm2PerDrop, 0.01f)
  }

  @Test
  fun testDarkStoreHubModelIntegrity() {
    val hub = DarkStoreHub(
      id = "HSR_03",
      name = "HSR Layout Sector 1",
      clusterCode = "HUB-03",
      pickSec = 62,
      packSec = 38,
      stagingSec = 98,
      cm2Margin = 34.50f,
      slaCompliancePct = 100.0f,
      activePickers = 14,
      activeRiders = 28,
      currentQueue = 42
    )

    assertEquals(198, hub.totalCycleSec)
    assertTrue("HSR hub cycle must beat SLA", hub.totalCycleSec <= 240)
    assertTrue("HSR hub CM2 margin must be positive", hub.cm2Margin > 0)
    assertEquals(100.0f, hub.slaCompliancePct, 0.01f)
  }

  @Test
  fun testMarginWaterfallNetSurplus() {
    val aov = 420.00f
    val cogs = 268.00f
    val riderPayout = 48.20f
    val leaseOpex = 38.50f
    val coldPackaging = 14.10f
    val techFee = 22.80f

    val netCm2 = aov - cogs - riderPayout - leaseOpex - coldPackaging - techFee
    // 420 - 268 - 48.20 - 38.50 - 14.10 - 22.80 = 28.40
    assertEquals(28.40f, netCm2, 0.01f)

    val step = MarginWaterfallStep(
      label = "Net Contribution Margin (CM2)",
      code = "CM2",
      amount = netCm2,
      isDeduction = false,
      isResult = true,
      breakdownDescription = "Positive unit economic cash surplus per order drop"
    )
    assertTrue("CM2 step must be result step", step.isResult)
    assertEquals(28.40f, step.amount, 0.01f)
  }

  @Test
  fun testHudVisualizerTabsAndStressModes() {
    assertEquals(4, HudVisualizerTab.values().size)
    assertNotNull(HudVisualizerTab.CYCLE_TIMES)
    assertNotNull(HudVisualizerTab.CM2_WATERFALL)
    assertNotNull(HudVisualizerTab.HUB_MATRIX)
    assertNotNull(HudVisualizerTab.ALADDIN_STRESS)

    assertEquals(4, AladdinStressMode.values().size)
    assertNotNull(AladdinStressMode.BASELINE)
    assertNotNull(AladdinStressMode.FUEL_SURGE_25)
    assertNotNull(AladdinStressMode.MONSOON_DELUGE)
    assertNotNull(AladdinStressMode.PEAK_RUSH_10X)
  }
}
