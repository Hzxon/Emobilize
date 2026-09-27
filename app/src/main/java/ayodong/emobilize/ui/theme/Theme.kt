package ayodong.emobilize.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun EmobilizeTheme(
    palette: AppPalette,
    content: @Composable () -> Unit,
) {
    val onPrimary = if (palette.id == 2) Color(0xFF0D0C18) else Color.White
    val colorScheme = if (palette.id == 2) {
        darkColorScheme(
            primary = palette.primary,
            onPrimary = onPrimary,
            secondary = palette.primaryDark,
            background = palette.surface,
            surface = palette.card,
            onBackground = palette.text,
            onSurface = palette.text,
        )
    } else {
        lightColorScheme(
            primary = palette.primary,
            onPrimary = onPrimary,
            secondary = palette.primaryDark,
            background = palette.surface,
            surface = palette.card,
            onBackground = palette.text,
            onSurface = palette.text,
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = palette.id == 1
                isAppearanceLightNavigationBars = palette.id == 1
            }
        }
    }

    CompositionLocalProvider(LocalPalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}
