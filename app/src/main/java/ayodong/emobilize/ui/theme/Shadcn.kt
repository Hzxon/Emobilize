package ayodong.emobilize.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * Semantic tokens from the shadcn registry item `@shadcn/theme-neutral`.
 * Values stay in the registry's HSL form so light and dark stay in sync with the source theme.
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

val ShadcnLight = ShadcnColors(
    background = hsl(0f, 0f, 100f),
    foreground = hsl(0f, 0f, 3.9f),
    card = hsl(0f, 0f, 100f),
    cardForeground = hsl(0f, 0f, 3.9f),
    popover = hsl(0f, 0f, 100f),
    popoverForeground = hsl(0f, 0f, 3.9f),
    primary = hsl(0f, 0f, 9f),
    primaryForeground = hsl(0f, 0f, 98f),
    secondary = hsl(0f, 0f, 96.1f),
    secondaryForeground = hsl(0f, 0f, 9f),
    muted = hsl(0f, 0f, 96.1f),
    mutedForeground = hsl(0f, 0f, 45.1f),
    accent = hsl(0f, 0f, 96.1f),
    accentForeground = hsl(0f, 0f, 9f),
    destructive = hsl(0f, 84.2f, 60.2f),
    destructiveForeground = hsl(0f, 0f, 98f),
    border = hsl(0f, 0f, 89.8f),
    input = hsl(0f, 0f, 89.8f),
    ring = hsl(0f, 0f, 3.9f),
    chart1 = hsl(12f, 76f, 61f),
    chart2 = hsl(173f, 58f, 39f),
    chart3 = hsl(197f, 37f, 24f),
    chart4 = hsl(43f, 74f, 66f),
    chart5 = hsl(27f, 87f, 67f),
)

val ShadcnDark = ShadcnColors(
    background = hsl(0f, 0f, 3.9f),
    foreground = hsl(0f, 0f, 98f),
    card = hsl(0f, 0f, 3.9f),
    cardForeground = hsl(0f, 0f, 98f),
    popover = hsl(0f, 0f, 3.9f),
    popoverForeground = hsl(0f, 0f, 98f),
    primary = hsl(0f, 0f, 98f),
    primaryForeground = hsl(0f, 0f, 9f),
    secondary = hsl(0f, 0f, 14.9f),
    secondaryForeground = hsl(0f, 0f, 98f),
    muted = hsl(0f, 0f, 14.9f),
    mutedForeground = hsl(0f, 0f, 63.9f),
    accent = hsl(0f, 0f, 14.9f),
    accentForeground = hsl(0f, 0f, 98f),
    destructive = hsl(0f, 62.8f, 30.6f),
    destructiveForeground = hsl(0f, 0f, 98f),
    border = hsl(0f, 0f, 14.9f),
    input = hsl(0f, 0f, 14.9f),
    ring = hsl(0f, 0f, 83.1f),
    chart1 = hsl(220f, 70f, 50f),
    chart2 = hsl(160f, 60f, 45f),
    chart3 = hsl(30f, 80f, 55f),
    chart4 = hsl(280f, 65f, 60f),
    chart5 = hsl(340f, 75f, 55f),
)

/** `--radius: 0.5rem`, with the shadcn sm/md/lg/xl offsets. */
object ShadcnRadius {
    val base = 8.dp
    val sm = 4.dp
    val md = 6.dp
    val lg = 8.dp
    val xl = 12.dp
}

/** Tailwind spacing scale used by shadcn layouts. */
object ShadcnSpace {
    val px = 1.dp
    val s1 = 4.dp
    val s2 = 8.dp
    val s3 = 12.dp
    val s4 = 16.dp
    val s5 = 20.dp
    val s6 = 24.dp
    val s8 = 32.dp
    val s10 = 40.dp
}
