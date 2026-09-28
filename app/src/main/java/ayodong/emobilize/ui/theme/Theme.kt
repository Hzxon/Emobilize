package ayodong.emobilize.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun EmobilizeTheme(
    palette: AppPalette,
    content: @Composable () -> Unit,
) {
    val tokens = palette.scheme
    val colorScheme = if (palette.id == 2) {
        darkColorScheme(
            primary = tokens.primary,
            onPrimary = tokens.primaryForeground,
            primaryContainer = tokens.secondary,
            onPrimaryContainer = tokens.secondaryForeground,
            secondary = tokens.secondary,
            onSecondary = tokens.secondaryForeground,
            secondaryContainer = tokens.muted,
            onSecondaryContainer = tokens.mutedForeground,
            tertiary = tokens.accent,
            onTertiary = tokens.accentForeground,
            background = tokens.background,
            onBackground = tokens.foreground,
            surface = tokens.card,
            onSurface = tokens.cardForeground,
            surfaceVariant = tokens.muted,
            onSurfaceVariant = tokens.mutedForeground,
            error = tokens.destructive,
            onError = tokens.destructiveForeground,
            outline = tokens.border,
            outlineVariant = tokens.input,
            scrim = tokens.foreground.copy(alpha = 0.5f),
        )
    } else {
        lightColorScheme(
            primary = tokens.primary,
            onPrimary = tokens.primaryForeground,
            primaryContainer = tokens.secondary,
            onPrimaryContainer = tokens.secondaryForeground,
            secondary = tokens.secondary,
            onSecondary = tokens.secondaryForeground,
            secondaryContainer = tokens.muted,
            onSecondaryContainer = tokens.mutedForeground,
            tertiary = tokens.accent,
            onTertiary = tokens.accentForeground,
            background = tokens.background,
            onBackground = tokens.foreground,
            surface = tokens.card,
            onSurface = tokens.cardForeground,
            surfaceVariant = tokens.muted,
            onSurfaceVariant = tokens.mutedForeground,
            error = tokens.destructive,
            onError = tokens.destructiveForeground,
            outline = tokens.border,
            outlineVariant = tokens.input,
            scrim = tokens.foreground.copy(alpha = 0.5f),
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
