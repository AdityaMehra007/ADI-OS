package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = TitanCyan,
  onPrimary = ObsidianDark,
  primaryContainer = SlateElevated,
  onPrimaryContainer = TitanCyan,
  secondary = TitanGold,
  onSecondary = ObsidianDark,
  secondaryContainer = SlateCard,
  onSecondaryContainer = TitanGoldLight,
  tertiary = TitanEmerald,
  onTertiary = ObsidianDark,
  background = ObsidianDark,
  onBackground = TextPrimaryDark,
  surface = SlateDarker,
  onSurface = TextPrimaryDark,
  surfaceVariant = SlateCard,
  onSurfaceVariant = TextSecondaryDark,
  outline = SlateBorder,
  error = TitanCrimson,
  onError = TextPrimaryDark
)

private val LightColorScheme = lightColorScheme(
  primary = TitanCyanMuted,
  onPrimary = SurfaceLight,
  primaryContainer = SlateLight,
  onPrimaryContainer = TextPrimaryLight,
  secondary = TitanGold,
  onSecondary = SurfaceLight,
  secondaryContainer = SlateLight,
  onSecondaryContainer = TextPrimaryLight,
  tertiary = TitanEmerald,
  onTertiary = SurfaceLight,
  background = SlateLight,
  onBackground = TextPrimaryLight,
  surface = SurfaceLight,
  onSurface = TextPrimaryLight,
  surfaceVariant = SlateLight,
  onSurfaceVariant = TextSecondaryLight,
  outline = BorderLight,
  error = TitanCrimson,
  onError = SurfaceLight
)

@Composable
fun TitanTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MyApplicationTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to sleek Titan Dark Terminal theme
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
