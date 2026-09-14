package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SkyboundColorScheme = darkColorScheme(
  primary = LanternBlue,
  onPrimary = MidnightSky,
  primaryContainer = DeepIndigo,
  onPrimaryContainer = MoonKoiSilver,
  secondary = AmberLight,
  onSecondary = MidnightSky,
  secondaryContainer = AmberWarm,
  onSecondaryContainer = Color.White,
  tertiary = VermilionSash,
  onTertiary = Color.White,
  background = MidnightSky,
  onBackground = MoonKoiWhite,
  surface = SurfaceDark,
  onSurface = MoonKoiSilver,
  surfaceVariant = DeepIndigo,
  onSurfaceVariant = MoonKoiSilver,
  outline = TealShrine
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  // Always use the atmospheric Skybound theme for the fantasy world
  MaterialTheme(
    colorScheme = SkyboundColorScheme,
    typography = Typography,
    content = content
  )
}
