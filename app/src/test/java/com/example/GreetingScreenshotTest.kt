package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.UserProfile
import com.example.ui.components.TitanTopBar
import com.example.ui.theme.TitanTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun titan_topbar_screenshot() {
    val sampleProfile = UserProfile(
      id = "adi_test",
      name = "Adi",
      educationDegree = "B.Tech Computer Science",
      educationSpecialization = "AI & Systems",
      university = "Bengaluru University",
      location = "Bengaluru, India",
      interests = "Distributed Systems",
      automationMode = "APPROVAL",
      brutalStrategyMode = false,
      profileCompleteness = 96
    )

    composeTestRule.setContent {
      TitanTheme {
        TitanTopBar(
          userProfile = sampleProfile,
          onToggleBrutalMode = {},
          onAutomationModeClick = {},
          onSearchClick = {}
        )
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/titan_topbar.png")
  }
}
