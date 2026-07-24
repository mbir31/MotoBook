package com.example.motobook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.motobook.presentation.navigation.NavGraph
import com.example.motobook.presentation.theme.MotoBookTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = applicationContext as MotoBookApplication
        val prefs = app.container.userPreferences

        setContent {
            val themeName by prefs.theme.collectAsState(initial = "FROST_LIGHT")
            val glassIntensity by prefs.glassIntensity.collectAsState(initial = 0.85f)
            val cardRadius by prefs.cardRadius.collectAsState(initial = 20f)

            MotoBookTheme(
                themeName = themeName,
                glassIntensity = glassIntensity,
                cardRadiusDp = cardRadius
            ) {
                NavGraph()
            }
        }
    }
}
