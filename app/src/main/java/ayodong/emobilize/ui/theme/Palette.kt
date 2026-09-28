package ayodong.emobilize.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

fun Color.at(alphaByte: Int): Color = copy(alpha = alphaByte / 255f)

fun AppPalette.inkOn(color: Color): Color {
    val luminance = 0.299f * color.red + 0.587f * color.green + 0.114f * color.blue
    return if (luminance > 0.72f) ink else onDark
}

data class AppPalette(
    val id: Int,
    val scheme: ShadcnColors,
    val primary: Color,
    val primaryDark: Color,
    val primaryLight: Color,
    val ring: List<Color>,
    val surface: Color,
    val card: Color,
    val text: Color,
    val textMuted: Color,
    val textSubtle: Color,
    val navBg: Color,
    val overlayBg: Color,
    val modalBg: Color,
    val signalInactive: Color,
    val homeIndicator: Color,
    val statusTodo: Color,
    val statusProgress: Color,
    val statusDone: Color,
    val catStudy: Color,
    val catWork: Color,
    val catHealth: Color,
    val catPersonal: Color,
    val catUniversity: Color,
    val prioLow: Color,
    val prioMedium: Color,
    val prioHigh: Color,
    val actionAdd: Color,
    val actionUpdate: Color,
    val actionEdit: Color,
    val selBgDelete: Color,
    val selBgUpdate: Color,
    val selBorderDelete: Color,
    val selBorderUpdate: Color,
    val cardSelBgDelete: Color,
    val cardSelBgUpdate: Color,
    val progBtnBg: Color,
    val progBtnColor: Color,
    val progBtnBorder: Color,
    val cubeTop: List<Color>,
    val cubeLeft: List<Color>,
    val cubeRight: List<Color>,
    val cubeEdge: Color,
    val cubeShine: Color,
    val cubeInner: List<Color>,
    val cubeGlow: Color,
    val onFilled: Color,
    val onDark: Color,
    val ink: Color,
    val danger: Color,
    val cardHighlight: Color,
    val shadow: Color,
)

private fun paletteFrom(id: Int, scheme: ShadcnColors, dark: Boolean): AppPalette {
    val scrim = if (dark) Color.Black.copy(alpha = 0.62f) else Color.Black.copy(alpha = 0.40f)
    return AppPalette(
        id = id,
        scheme = scheme,
        primary = scheme.primary,
        primaryDark = scheme.foreground,
        primaryLight = scheme.muted,
        ring = if (dark) {
            listOf(scheme.background, scheme.foreground, scheme.secondary, scheme.mutedForeground, scheme.background)
        } else {
            listOf(
                scheme.primary,
                scheme.mutedForeground,
                scheme.border,
                scheme.background,
                scheme.border,
                scheme.mutedForeground,
                scheme.primary,
            )
        },
        surface = scheme.background,
        card = scheme.card,
        text = scheme.foreground,
        textMuted = scheme.mutedForeground,
        textSubtle = scheme.mutedForeground.copy(alpha = 0.62f),
        navBg = scheme.background.copy(alpha = if (dark) 0.97f else 0.96f),
        overlayBg = scrim,
        modalBg = scrim,
        signalInactive = scheme.border,
        homeIndicator = scheme.primary,
        statusTodo = scheme.mutedForeground,
        statusProgress = scheme.chart1,
        statusDone = scheme.chart2,
        catStudy = scheme.chart1,
        catWork = scheme.chart5,
        catHealth = scheme.chart2,
        catPersonal = scheme.chart4,
        catUniversity = scheme.chart3,
        prioLow = scheme.chart2,
        prioMedium = scheme.chart4,
        prioHigh = scheme.destructive,
        actionAdd = scheme.primary,
        actionUpdate = scheme.chart3,
        actionEdit = scheme.chart4,
        selBgDelete = scheme.destructive.copy(alpha = 0.12f),
        selBgUpdate = scheme.chart4.copy(alpha = 0.16f),
        selBorderDelete = scheme.destructive.copy(alpha = 0.45f),
        selBorderUpdate = scheme.chart4.copy(alpha = 0.55f),
        cardSelBgDelete = scheme.destructive.copy(alpha = 0.10f),
        cardSelBgUpdate = scheme.chart4.copy(alpha = 0.12f),
        progBtnBg = scheme.chart1.copy(alpha = 0.12f),
        progBtnColor = scheme.chart1,
        progBtnBorder = scheme.chart1.copy(alpha = 0.40f),
        cubeTop = if (dark) {
            listOf(scheme.foreground, scheme.mutedForeground)
        } else {
            listOf(scheme.mutedForeground, scheme.primary)
        },
        cubeLeft = if (dark) {
            listOf(scheme.secondary, scheme.background)
        } else {
            listOf(scheme.primary, scheme.foreground)
        },
        cubeRight = if (dark) {
            listOf(scheme.foreground, scheme.mutedForeground)
        } else {
            listOf(scheme.border, scheme.mutedForeground)
        },
        cubeEdge = if (dark) scheme.foreground else scheme.background,
        cubeShine = scheme.foreground,
        cubeInner = listOf(scheme.background, if (dark) scheme.secondary else scheme.muted),
        cubeGlow = if (dark) scheme.foreground.copy(alpha = 0.35f) else Color.Transparent,
        onFilled = scheme.primaryForeground,
        onDark = hsl(0f, 0f, 98f),
        ink = if (dark) scheme.background else scheme.foreground,
        danger = scheme.destructive,
        cardHighlight = Color.Transparent,
        shadow = Color.Black.copy(alpha = if (dark) 0.35f else 0.06f),
    )
}

val NeutralLight = paletteFrom(1, ShadcnLight, dark = false)
val NeutralDark = paletteFrom(2, ShadcnDark, dark = true)

val LocalPalette = staticCompositionLocalOf { NeutralLight }

fun AppPalette.categoryColor(category: ayodong.emobilize.model.Category): Color = when (category) {
    ayodong.emobilize.model.Category.Study -> catStudy
    ayodong.emobilize.model.Category.Work -> catWork
    ayodong.emobilize.model.Category.Health -> catHealth
    ayodong.emobilize.model.Category.Personal -> catPersonal
    ayodong.emobilize.model.Category.University -> catUniversity
}

fun AppPalette.priorityColor(priority: ayodong.emobilize.model.Priority): Color = when (priority) {
    ayodong.emobilize.model.Priority.Low -> prioLow
    ayodong.emobilize.model.Priority.Medium -> prioMedium
    ayodong.emobilize.model.Priority.High -> prioHigh
}
