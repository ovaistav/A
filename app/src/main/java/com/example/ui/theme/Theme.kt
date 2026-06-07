package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(primary = Purple80, secondary = PurpleGrey80, tertiary = Pink80)

private val LightColorScheme =
  lightColorScheme(
    primary = Color(0xFF65558F), // Beautiful Deep Lavender
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF3E7FF), // Soft Lilac Canvas accent
    onPrimaryContainer = Color(0xFF21005D),
    secondary = Color(0xFF49454F), // Elegant Medium Slate Slate
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8DEF8), // Pastel Lavender Button/Tab fill
    onSecondaryContainer = Color(0xFF21005D),
    background = Color(0xFFFDF8F6), // Warm Soft Cream and Peach backdrop
    onBackground = Color(0xFF1D1B20), // Rich Obsidian/Deep Slate Text
    surface = Color(0xFFFFFFFF), // Pure White Panel Contrast Cards
    onSurface = Color(0xFF1D1B20),
    outline = Color(0xFFCAC4D0), // Delicate minimalist frame borders
    outlineVariant = Color(0xFFD0BCFF) // Highlighted borders
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic colors by default so our custom premium Clean Minimalism theme takes full effect!
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
