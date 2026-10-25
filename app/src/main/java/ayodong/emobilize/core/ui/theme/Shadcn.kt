package ayodong.emobilize.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * Semantic tokens in shadcn's shape, filled with the Departures palette.
 * hsl() stays for callers that build tones from HSL.
 */
fun hsl(hue: Float, saturation: Float, lightness: Float): Color {
    val s = saturation / 100f
    val l = lightness / 100f
    val chroma = (1f - abs(2f * l - 1f)) * s
    val huePrime = ((hue % 360f) / 60f)
    val x = chroma * (1f - abs(huePrime % 2f - 1f))
    val match = l - chroma / 2f
    val (red, green, blue) = when {
        huePrime < 1f -> Triple(chroma, x, 0f)
        huePrime < 2f -> Triple(x, chroma, 0f)
        huePrime < 3f -> Triple(0f, chroma, x)
        huePrime < 4f -> Triple(0f, x, chroma)
        huePrime < 5f -> Triple(x, 0f, chroma)
        else -> Triple(chroma, 0f, x)
    }
    return Color(red + match, green + match, blue + match)
}

data class ShadcnColors(
    val background: Color,
    val foreground: Color,
    val card: Color,
    val cardForeground: Color,
    val popover: Color,
    val popoverForeground: Color,
    val primary: Color,
    val primaryForeground: Color,
    val secondary: Color,
    val secondaryForeground: Color,
    val muted: Color,
    val mutedForeground: Color,
    val accent: Color,
    val accentForeground: Color,
    val destructive: Color,
    val destructiveForeground: Color,
    val border: Color,
    val input: Color,
    val ring: Color,
    val chart1: Color,
    val chart2: Color,
    val chart3: Color,
    val chart4: Color,
    val chart5: Color,
)

/** Departures light: grey station wall, white panels, black ink, signal yellow on black. */
val ShadcnLight = ShadcnColors(
    background = Color(0xFFEEF0F2),
    foreground = Color(0xFF111214),
    card = Color(0xFFFFFFFF),
    cardForeground = Color(0xFF111214),
    popover = Color(0xFFFFFFFF),
    popoverForeground = Color(0xFF111214),
    primary = Color(0xFF111214),
    primaryForeground = Color(0xFFFFD400),
    secondary = Color(0xFFFFFFFF),
    secondaryForeground = Color(0xFF111214),
    muted = Color(0xFFE3E6EA),
    mutedForeground = Color(0xFF4A4D55),
    accent = Color(0xFFFFD400),
    accentForeground = Color(0xFF111214),
    destructive = Color(0xFFC4161C),
    destructiveForeground = Color(0xFFFFFFFF),
    border = Color(0xFF111214),
    input = Color(0xFF111214),
    ring = Color(0xFFFFD400),
    chart1 = Color(0xFFD6001C),
    chart2 = Color(0xFF007A4D),
    chart3 = Color(0xFF5C2D91),
    chart4 = Color(0xFFB35900),
    chart5 = Color(0xFF0057B8),
)

/** Departures dark: the station after hours, black panels with yellow signal. */
val ShadcnDark = ShadcnColors(
    background = Color(0xFF111214),
    foreground = Color(0xFFFFFFFF),
    card = Color(0xFF1B1D21),
    cardForeground = Color(0xFFFFFFFF),
    popover = Color(0xFF1B1D21),
    popoverForeground = Color(0xFFFFFFFF),
    primary = Color(0xFFFFD400),
    primaryForeground = Color(0xFF111214),
    secondary = Color(0xFF22242A),
    secondaryForeground = Color(0xFFFFFFFF),
    muted = Color(0xFF22242A),
    mutedForeground = Color(0xFFA9ACB3),
    accent = Color(0xFFFFD400),
    accentForeground = Color(0xFF111214),
    destructive = Color(0xFFFF7A5C),
    destructiveForeground = Color(0xFF111214),
    border = Color(0xFF5C5F66),
    input = Color(0xFF5C5F66),
    ring = Color(0xFFFFD400),
    chart1 = Color(0xFFFF5A6A),
    chart2 = Color(0xFF3DDC84),
    chart3 = Color(0xFFB18CFF),
    chart4 = Color(0xFFFFB020),
    chart5 = Color(0xFF5AA2FF),
)

/** Departures draws square corners everywhere; round shapes are only line dots and stops. */
object ShadcnRadius {
    val base = 0.dp
    val sm = 0.dp
    val md = 0.dp
    val lg = 0.dp
    val xl = 0.dp
}

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(ShadcnRadius.sm),
    small = RoundedCornerShape(ShadcnRadius.md),
    medium = RoundedCornerShape(ShadcnRadius.lg),
    large = RoundedCornerShape(ShadcnRadius.xl),
    extraLarge = RoundedCornerShape(ShadcnRadius.xl),
)
