package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.screens.CalculatorMainScreen
import com.example.ui.theme.LiquidGlassCalculatorTheme
import com.example.viewmodel.CalculatorViewModel
import com.example.viewmodel.ConvertersViewModel
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
  fun greeting_screenshot() {
    val calcViewModel = CalculatorViewModel()
    val toolsViewModel = ConvertersViewModel()

    composeTestRule.setContent {
      LiquidGlassCalculatorTheme(darkTheme = true) {
        CalculatorMainScreen(
          calcViewModel = calcViewModel,
          toolsViewModel = toolsViewModel
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
