package com.example.util

import android.view.KeyEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Handles global hardware / emulator keyboard shortcuts for the 25-Keypad directives.
 *
 * Supports:
 * - Two-digit sequence commands: '01', '02', ..., '25'
 * - Single-digit followed by Enter: '1' + Enter -> Directive 01
 * - Direct Alt+Number shortcuts: Alt+1..9 -> Directives 01..09
 * - Keyboard shortcut helper: 'K' or '?' toggles the 25-Keypad Shortcut Matrix
 */
class KeypadShortcutDetector(
  private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main),
  private val onDirectiveTriggered: (KeypadDirective) -> Unit,
  private val onBufferUpdated: (String) -> Unit = {},
  private val onToggleCheatSheet: () -> Unit = {}
) {

  private val buffer = StringBuilder()
  private var resetJob: Job? = null
  private val bufferTimeoutMs = 1800L

  /**
   * Dispatches a KeyEvent and returns true if the key was consumed as a keypad shortcut.
   */
  fun handleKeyEvent(event: KeyEvent): Boolean {
    if (event.action != KeyEvent.ACTION_DOWN) {
      return false
    }

    // Toggle cheat-sheet with 'K', 'k', or '?'
    if (!event.isAltPressed && !event.isCtrlPressed) {
      if (event.keyCode == KeyEvent.KEYCODE_K) {
        onToggleCheatSheet()
        return true
      }
    }

    // Direct Alt + digit shortcut (Alt+1 -> 01, Alt+9 -> 09)
    if (event.isAltPressed) {
      val digit = keycodeToDigit(event.keyCode)
      if (digit != null && digit in 1..9) {
        val directive = KeypadDirectiveRegistry.findByCode("0$digit")
        if (directive != null) {
          clearBuffer()
          onDirectiveTriggered(directive)
          return true
        }
      }
    }

    // Enter key executes whatever single digit is in the buffer
    if (event.keyCode == KeyEvent.KEYCODE_ENTER || event.keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
      if (buffer.isNotEmpty()) {
        val code = buffer.toString()
        clearBuffer()
        val directive = KeypadDirectiveRegistry.findByCode(code)
        if (directive != null) {
          onDirectiveTriggered(directive)
          return true
        }
      }
      return false
    }

    // Backspace / Escape clears pending buffer
    if (event.keyCode == KeyEvent.KEYCODE_ESCAPE || (event.keyCode == KeyEvent.KEYCODE_BACK && buffer.isNotEmpty())) {
      clearBuffer()
      return true
    }

    // Check if key is a digit (0..9)
    val digit = keycodeToDigit(event.keyCode) ?: return false

    // Append digit to buffer
    buffer.append(digit)
    val current = buffer.toString()
    onBufferUpdated(current)

    // Reset buffer after timeout
    resetJob?.cancel()
    resetJob = scope.launch {
      delay(bufferTimeoutMs)
      clearBuffer()
    }

    // If 2 digits entered (e.g. "01", "02", ..., "25")
    if (current.length >= 2) {
      val directive = KeypadDirectiveRegistry.findByCode(current)
      clearBuffer()
      if (directive != null) {
        onDirectiveTriggered(directive)
        return true
      } else {
        // Two digits that do not form a valid directive (e.g. "26".."99")
        return false
      }
    }

    // Single digit entered.
    // If the digit is 3..9, it can never form a valid 1..25 module when followed by another digit,
    // so we can schedule execution of directive 03..09 after a short debounce if Enter isn't pressed.
    if (digit in 3..9) {
      resetJob?.cancel()
      resetJob = scope.launch {
        delay(900L)
        if (buffer.toString() == digit.toString()) {
          val directive = KeypadDirectiveRegistry.findByCode("0$digit")
          clearBuffer()
          if (directive != null) {
            onDirectiveTriggered(directive)
          }
        }
      }
    }

    return true
  }

  fun clearBuffer() {
    resetJob?.cancel()
    buffer.clear()
    onBufferUpdated("")
  }

  private fun keycodeToDigit(keyCode: Int): Int? {
    return when (keyCode) {
      KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_NUMPAD_0 -> 0
      KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_NUMPAD_1 -> 1
      KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_NUMPAD_2 -> 2
      KeyEvent.KEYCODE_3, KeyEvent.KEYCODE_NUMPAD_3 -> 3
      KeyEvent.KEYCODE_4, KeyEvent.KEYCODE_NUMPAD_4 -> 4
      KeyEvent.KEYCODE_5, KeyEvent.KEYCODE_NUMPAD_5 -> 5
      KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_NUMPAD_6 -> 6
      KeyEvent.KEYCODE_7, KeyEvent.KEYCODE_NUMPAD_7 -> 7
      KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_NUMPAD_8 -> 8
      KeyEvent.KEYCODE_9, KeyEvent.KEYCODE_NUMPAD_9 -> 9
      else -> null
    }
  }
}
