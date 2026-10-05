package com.example

import android.app.Application
import android.view.KeyEvent
import androidx.test.core.app.ApplicationProvider
import com.example.ui.viewmodel.TitanScreen
import com.example.ui.viewmodel.TitanViewModel
import com.example.util.KeypadDirective
import com.example.util.KeypadDirectiveRegistry
import com.example.util.KeypadShortcutDetector
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class KeypadDirectiveShortcutTest {

  private lateinit var application: Application
  private lateinit var testScope: TestScope

  @Before
  fun setUp() {
    application = ApplicationProvider.getApplicationContext()
    testScope = TestScope(StandardTestDispatcher())
  }

  @Test
  fun testRegistryContainsAll25Directives() {
    val all = KeypadDirectiveRegistry.DIRECTIVES
    assertEquals("Must contain exactly 25 directives", 25, all.size)

    for (i in 1..25) {
      val code = if (i < 10) "0$i" else "$i"
      val foundByFormatted = KeypadDirectiveRegistry.findByCode(code)
      val foundByNumber = KeypadDirectiveRegistry.findByCode(i.toString())

      assertNotNull("Directive $code should be found by formatted code", foundByFormatted)
      assertNotNull("Directive $code should be found by raw number", foundByNumber)
      assertEquals(i, foundByFormatted?.id)
      assertEquals(code, foundByFormatted?.code)
      assertTrue("Directive title must not be blank", foundByFormatted?.title?.isNotBlank() == true)
      assertTrue("Directive summary must not be blank", foundByFormatted?.summary?.isNotBlank() == true)
    }
  }

  @Test
  fun testRegistryLookupsAndEdgeCases() {
    val d01 = KeypadDirectiveRegistry.findByCode("01")
    assertEquals("JD Decompiler", d01?.title)

    val d02 = KeypadDirectiveRegistry.findByCode("02")
    assertEquals("ATS Resume Synthesizer", d02?.title)

    val d18 = KeypadDirectiveRegistry.findByCode("18")
    assertEquals("Dark Store Daily Health Audit", d18?.title)

    val d25 = KeypadDirectiveRegistry.findByCode("25")
    assertEquals("Fast-Track Promotion Business Case", d25?.title)

    assertNull("00 should not resolve to a directive", KeypadDirectiveRegistry.findByCode("00"))
    assertNull("26 should not resolve to a directive", KeypadDirectiveRegistry.findByCode("26"))
    assertNull("99 should not resolve to a directive", KeypadDirectiveRegistry.findByCode("99"))
    assertNull("Invalid text should not resolve", KeypadDirectiveRegistry.findByCode("abc"))
  }

  @Test
  fun testShortcutDetectorTwoDigitSequence01() = testScope.runTest {
    var triggeredDirective: KeypadDirective? = null
    var lastBuffer = ""

    val detector = KeypadShortcutDetector(
      scope = testScope,
      onDirectiveTriggered = { triggeredDirective = it },
      onBufferUpdated = { lastBuffer = it }
    )

    // Press '0'
    val consumed0 = detector.handleKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_0))
    assertTrue("Key '0' should be consumed by detector", consumed0)
    assertEquals("0", lastBuffer)
    assertNull(triggeredDirective)

    // Press '1'
    val consumed1 = detector.handleKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_1))
    assertTrue("Key '1' should be consumed by detector", consumed1)
    advanceUntilIdle()

    assertNotNull("Directive 01 should be triggered", triggeredDirective)
    assertEquals(1, triggeredDirective?.id)
    assertEquals("01", triggeredDirective?.code)
    assertEquals("JD Decompiler", triggeredDirective?.title)
    assertEquals("", lastBuffer)
  }

  @Test
  fun testShortcutDetectorTwoDigitSequence18() = testScope.runTest {
    var triggeredDirective: KeypadDirective? = null

    val detector = KeypadShortcutDetector(
      scope = testScope,
      onDirectiveTriggered = { triggeredDirective = it }
    )

    detector.handleKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_1))
    detector.handleKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_8))
    advanceUntilIdle()

    assertNotNull("Directive 18 should be triggered", triggeredDirective)
    assertEquals(18, triggeredDirective?.id)
    assertEquals("Dark Store Daily Health Audit", triggeredDirective?.title)
  }

  @Test
  fun testShortcutDetectorSingleDigitPlusEnter() = testScope.runTest {
    var triggeredDirective: KeypadDirective? = null

    val detector = KeypadShortcutDetector(
      scope = testScope,
      onDirectiveTriggered = { triggeredDirective = it }
    )

    // Press '2'
    detector.handleKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_2))
    assertNull(triggeredDirective)

    // Press Enter
    val consumedEnter = detector.handleKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
    assertTrue(consumedEnter)
    advanceUntilIdle()

    assertNotNull("Directive 02 should be triggered via Enter", triggeredDirective)
    assertEquals(2, triggeredDirective?.id)
    assertEquals("ATS Resume Synthesizer", triggeredDirective?.title)
  }

  @Test
  fun testShortcutDetectorToggleCheatSheet() = testScope.runTest {
    var cheatSheetToggled = false

    val detector = KeypadShortcutDetector(
      scope = testScope,
      onDirectiveTriggered = {},
      onToggleCheatSheet = { cheatSheetToggled = true }
    )

    val consumedK = detector.handleKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_K))
    assertTrue(consumedK)
    assertTrue("Pressing K should toggle cheat sheet", cheatSheetToggled)
  }

  @Test
  fun testViewModelTriggerKeypadDirective() {
    val factory = com.example.ui.viewmodel.TitanViewModelFactory.createFactory(application)
    val viewModel = factory.create(TitanViewModel::class.java)

    // Trigger Directive 01 via code
    val success = viewModel.triggerKeypadDirective("01")
    assertTrue("Triggering '01' should succeed", success)

    // Verified state mutations
    assertEquals(TitanScreen.COPILOT, viewModel.currentScreen.value)
    assertEquals("01", viewModel.lastTriggeredKeypadDirective.value?.code)
    assertEquals("JD Decompiler", viewModel.lastTriggeredKeypadDirective.value?.title)
    assertEquals("", viewModel.keypadShortcutBuffer.value)
    assertFalse(viewModel.isKeypadCheatSheetOpen.value)

    // Verify Copilot received message
    val messages = viewModel.chatMessages.value
    assertTrue("Chat messages should contain sent directive", messages.any { it.text.contains("JD Decompiler") })

    // Verify Telemetry
    val telemetry = viewModel.quickActionTelemetry.value
    assertNotNull(telemetry)
    assertTrue(telemetry?.taskName?.contains("01") == true)
  }
}
