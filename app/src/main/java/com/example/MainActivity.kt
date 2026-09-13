package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.screens.CalculatorMainScreen
import com.example.ui.theme.LiquidGlassCalculatorTheme
import com.example.viewmodel.CalculatorViewModel
import com.example.viewmodel.ConvertersViewModel

class MainActivity : ComponentActivity() {

    private val calcViewModel: CalculatorViewModel by viewModels()
    private val toolsViewModel: ConvertersViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val calcState by calcViewModel.uiState.collectAsState()

            LiquidGlassCalculatorTheme(darkTheme = calcState.isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    CalculatorMainScreen(
                        calcViewModel = calcViewModel,
                        toolsViewModel = toolsViewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
