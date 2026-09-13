package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.math.AngleMode
import com.example.math.ExpressionEvaluator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Liquid Calc", appName)
  }

  @Test
  fun `standard math evaluation`() {
    val res1 = ExpressionEvaluator.evaluate("2+3*4")
    assertTrue(res1 is ExpressionEvaluator.Result.Success)
    assertEquals("14", (res1 as ExpressionEvaluator.Result.Success).formatted)

    val res2 = ExpressionEvaluator.evaluate("10÷4")
    assertTrue(res2 is ExpressionEvaluator.Result.Success)
    assertEquals("2.5", (res2 as ExpressionEvaluator.Result.Success).formatted)
  }

  @Test
  fun `scientific math evaluation`() {
    val sinRes = ExpressionEvaluator.evaluate("sin(30)", AngleMode.DEG)
    assertTrue(sinRes is ExpressionEvaluator.Result.Success)
    assertEquals("0.5", (sinRes as ExpressionEvaluator.Result.Success).formatted)

    val sqrtRes = ExpressionEvaluator.evaluate("√(144)")
    assertTrue(sqrtRes is ExpressionEvaluator.Result.Success)
    assertEquals("12", (sqrtRes as ExpressionEvaluator.Result.Success).formatted)

    val factRes = ExpressionEvaluator.evaluate("5!")
    assertTrue(factRes is ExpressionEvaluator.Result.Success)
    assertEquals("120", (factRes as ExpressionEvaluator.Result.Success).formatted)

    val powRes = ExpressionEvaluator.evaluate("2^10")
    assertTrue(powRes is ExpressionEvaluator.Result.Success)
    assertEquals("1,024", (powRes as ExpressionEvaluator.Result.Success).formatted)
  }

  @Test
  fun `live preview safe evaluation`() {
    val preview = ExpressionEvaluator.evaluatePreview("25*4+", AngleMode.DEG)
    assertEquals("100", preview)
  }
}
