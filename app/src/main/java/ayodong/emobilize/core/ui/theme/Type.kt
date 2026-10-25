package ayodong.emobilize.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import ayodong.emobilize.R

// Inter Tight ships as one variable file, so each weight points the wght axis at the same resource.
@OptIn(ExperimentalTextApi::class)
private fun interTight(weight: FontWeight) = Font(
    R.font.inter_tight,
    weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

val AppFont = FontFamily(
    interTight(FontWeight.Normal),
    interTight(FontWeight.Medium),
    interTight(FontWeight.SemiBold),
    interTight(FontWeight.Bold),
    interTight(FontWeight.ExtraBold),
)

/** Board figures: times, codes and status words, set like a departure display. */
val MonoFont = FontFamily(
    Font(R.font.ibm_plex_mono_medium, FontWeight.Medium),
    Font(R.font.ibm_plex_mono_semibold, FontWeight.SemiBold),
)

fun appStyle(
    size: TextUnit,
    weight: FontWeight = FontWeight.Normal,
    letterSpacing: TextUnit = 0.sp,
    lineHeight: TextUnit = TextUnit.Unspecified,
) = TextStyle(
    fontFamily = AppFont,
    fontSize = size,
    fontWeight = weight,
    letterSpacing = letterSpacing,
    lineHeight = lineHeight,
)

fun monoStyle(
    size: TextUnit,
    weight: FontWeight = FontWeight.SemiBold,
    letterSpacing: TextUnit = 0.sp,
) = TextStyle(
    fontFamily = MonoFont,
    fontSize = size,
    fontWeight = weight,
    letterSpacing = letterSpacing,
    fontFeatureSettings = "tnum",
)

/** Tailwind type scale used by shadcn, set in Inter Tight. */
object ShadcnType {
    val xs = appStyle(12.sp, lineHeight = 16.sp)
    val sm = appStyle(14.sp, lineHeight = 20.sp)
    val base = appStyle(16.sp, lineHeight = 24.sp)
    val lg = appStyle(18.sp, lineHeight = 28.sp)
    val xl = appStyle(20.sp, lineHeight = 28.sp)
    val xl2 = appStyle(24.sp, lineHeight = 32.sp)
    val xl3 = appStyle(30.sp, lineHeight = 36.sp)
    val xl4 = appStyle(36.sp, lineHeight = 40.sp)
}

val Typography = Typography(
    displayLarge = ShadcnType.xl4,
    displayMedium = ShadcnType.xl3,
    displaySmall = ShadcnType.xl2,
    headlineLarge = appStyle(32.sp, lineHeight = 40.sp),
    headlineMedium = appStyle(28.sp, lineHeight = 36.sp),
    headlineSmall = ShadcnType.xl2,
    titleLarge = appStyle(22.sp, lineHeight = 28.sp),
    titleMedium = appStyle(16.sp, FontWeight.Medium, lineHeight = 24.sp, letterSpacing = 0.15.sp),
    titleSmall = appStyle(14.sp, FontWeight.Medium, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    bodyLarge = appStyle(16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
    bodyMedium = appStyle(14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp),
    bodySmall = appStyle(12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp),
    labelLarge = appStyle(14.sp, FontWeight.Medium, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    labelMedium = appStyle(12.sp, FontWeight.Medium, lineHeight = 16.sp, letterSpacing = 0.5.sp),
    labelSmall = appStyle(11.sp, FontWeight.Medium, lineHeight = 16.sp, letterSpacing = 0.5.sp),
)
