package ayodong.emobilize.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import ayodong.emobilize.R

val AppFont = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold),
    Font(R.font.montserrat_bold, FontWeight.Bold),
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

/** Tailwind type scale used by shadcn, set in Montserrat. */
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
