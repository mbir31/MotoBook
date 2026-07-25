package com.example.motobook

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.motobook.presentation.navigation.NavGraph
import com.example.motobook.presentation.theme.MotoBookTheme
import java.util.Locale

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
            val language by prefs.language.collectAsState(initial = "en")

            val context = LocalContext.current
            val localizedContext = remember(language) {
                val locale = if (language == "bn") Locale("bn") else Locale("en")
                Locale.setDefault(locale)
                val config = Configuration(context.resources.configuration)
                config.setLocale(locale)
                context.createConfigurationContext(config)
            }

            CompositionLocalProvider(LocalContext provides localizedContext) {
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
}
