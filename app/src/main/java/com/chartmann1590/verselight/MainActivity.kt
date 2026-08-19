package com.chartmann1590.verselight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.chartmann1590.verselight.ui.VerseLightRoot
import com.chartmann1590.verselight.ui.theme.VerseLightTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { VerseLightTheme { VerseLightRoot() } }
    }
}

