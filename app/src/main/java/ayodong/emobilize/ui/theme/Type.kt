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

val Typography = Typography(
    bodyLarge = appStyle(16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
    bodyMedium = appStyle(14.sp),
    titleLarge = appStyle(22.sp, FontWeight.Normal),
    labelSmall = appStyle(11.sp, FontWeight.Medium),
)
