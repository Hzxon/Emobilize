package ayodong.emobilize.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val Danger = Color(0xFFEF4444)

fun Color.at(alphaByte: Int): Color = copy(alpha = alphaByte / 255f)

fun AppPalette.inkOn(color: Color): Color {
    val luminance = 0.299f * color.red + 0.587f * color.green + 0.114f * color.blue
    return if (luminance > 0.72f) ink else onDark
}

data class AppPalette(
    val id: Int,
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

val SagePalette = AppPalette(
    id = 1,
    primary = Color(0xFF8B9A6E),
    primaryDark = Color(0xFF6B7A4E),
    primaryLight = Color(0xFFB4C890),
    ring = listOf(
        Color(0xFF6B7A4E), Color(0xFF8B9A6E), Color(0xFFB4C890), Color(0xFFF7F2EB),
        Color(0xFFB4C890), Color(0xFF8B9A6E), Color(0xFF6B7A4E),
    ),
    surface = Color(0xFFF7F2EB),
    card = Color(0xFFFFFFFF),
    text = Color(0xFF1A1A14),
    textMuted = Color(0xFF1A1A14).copy(alpha = 0.38f),
    textSubtle = Color(0xFF1A1A14).copy(alpha = 0.30f),
    navBg = Color(0xFFF7F2EB).copy(alpha = 0.96f),
    overlayBg = Color(0xFFF7F2EB).copy(alpha = 0.72f),
    modalBg = Color(0xFFF7F2EB).copy(alpha = 0.55f),
    signalInactive = Color(0xFFC0BAB0),
    homeIndicator = Color(0xFF6B7A4E),
    statusTodo = Color(0xFF8A8A84),
    statusProgress = Color(0xFF4F7DB8),
    statusDone = Color(0xFF6F9565),
    catStudy = Color(0xFF8B9A6E),
    catWork = Color(0xFFC4A46E),
    catHealth = Color(0xFF7AAE8A),
    catPersonal = Color(0xFFA87C9E),
    catUniversity = Color(0xFF8E9AAE),
    prioLow = Color(0xFF7AAE8A),
    prioMedium = Color(0xFFD4A017),
    prioHigh = Danger,
    actionAdd = Color(0xFF8B9A6E),
    actionUpdate = Color(0xFFD4A017),
    actionEdit = Color(0xFF6B7A4E),
    selBgDelete = Color(0xFFFEF2F2),
    selBgUpdate = Color(0xFFFFFBEB),
    selBorderDelete = Color(0xFFFCA5A5),
    selBorderUpdate = Color(0xFFF59E0B).at(0x60),
    cardSelBgDelete = Color(0xFFFFF5F5),
    cardSelBgUpdate = Color(0xFFFFFBEB),
    progBtnBg = Color(0xFFEEF5FC),
    progBtnColor = Color(0xFF3F6FA9),
    progBtnBorder = Color(0xFF4F7DB8).at(0x60),
    cubeTop = listOf(Color(0xFF8B9A6E), Color(0xFF6B7A4E)),
    cubeLeft = listOf(Color(0xFF6B7A4E), Color(0xFF3A4428)),
    cubeRight = listOf(Color(0xFF4A5830), Color(0xFF2C3620)),
    cubeEdge = Color(0xFFF7F2EB),
    cubeShine = Color(0xFFF7F2EB),
    cubeInner = listOf(Color(0xFFF7F2EB), Color(0xFFB4C890)),
    cubeGlow = Color.Transparent,
    onFilled = Color.White,
    onDark = Color.White,
    ink = Color(0xFF1A1A14),
    danger = Danger,
    cardHighlight = Color.Transparent,
    shadow = Color.Black.copy(alpha = 0.055f),
)

val HoloPalette = AppPalette(
    id = 2,
    primary = Color(0xFFFFFFFF),
    primaryDark = Color(0xFFF4FEFF),
    primaryLight = Color(0xFFFFFFFF),
    ring = listOf(
        Color(0xFF050505), Color(0xFFFFFFFF), Color(0xFF1A1A1A),
        Color(0xFFF7F7F7), Color(0xFF050505),
    ),
    surface = Color(0xFF0D0C18),
    card = Color(0xFF1C1A2C),
    text = Color(0xFFF0EEFF),
    textMuted = Color(0xFFF0EEFF).copy(alpha = 0.38f),
    textSubtle = Color(0xFFF0EEFF).copy(alpha = 0.30f),
    navBg = Color(0xFF0D0C18).copy(alpha = 0.97f),
    overlayBg = Color(0xFF0D0C18).copy(alpha = 0.82f),
    modalBg = Color(0xFF0D0C18).copy(alpha = 0.65f),
    signalInactive = Color(0xFFF0EEFF).copy(alpha = 0.30f),
    homeIndicator = Color(0xFFFFFFFF),
    statusTodo = Color(0xFF7A78A0),
    statusProgress = Color(0xFF00D4FF),
    statusDone = Color(0xFF39FF7A),
    catStudy = Color(0xFF00D4FF),
    catWork = Color(0xFFFF7B35),
    catHealth = Color(0xFF39FF7A),
    catPersonal = Color(0xFFC678FF),
    catUniversity = Color(0xFF9B5CF6),
    prioLow = Color(0xFF39FF7A),
    prioMedium = Color(0xFFFFD600),
    prioHigh = Color(0xFFFF4444),
    actionAdd = Color(0xFF00D4FF),
    actionUpdate = Color(0xFFFF7B35),
    actionEdit = Color(0xFF9B5CF6),
    selBgDelete = Danger.copy(alpha = 0.12f),
    selBgUpdate = Color(0xFFFFD600).copy(alpha = 0.10f),
    selBorderDelete = Danger.copy(alpha = 0.35f),
    selBorderUpdate = Color(0xFFFFD600).copy(alpha = 0.30f),
    cardSelBgDelete = Danger.copy(alpha = 0.10f),
    cardSelBgUpdate = Color(0xFFFFD600).copy(alpha = 0.08f),
    progBtnBg = Color(0xFF00D4FF).copy(alpha = 0.12f),
    progBtnColor = Color(0xFF00D4FF),
    progBtnBorder = Color(0xFF00D4FF).copy(alpha = 0.40f),
    cubeTop = listOf(Color(0xFFFFFFFF), Color(0xFFE6E6E6)),
    cubeLeft = listOf(Color(0xFF3A3A3A), Color(0xFF050505)),
    cubeRight = listOf(Color(0xFFF5F5F5), Color(0xFF8E8E8E)),
    cubeEdge = Color(0xFFFFFFFF),
    cubeShine = Color.White,
    cubeInner = listOf(Color(0xFF141414), Color(0xFF050505)),
    cubeGlow = Color.White.copy(alpha = 0.55f),
    onFilled = Color(0xFF0D0C18),
    onDark = Color.White,
    ink = Color(0xFF0D0C18),
    danger = Danger,
    cardHighlight = Color.White.copy(alpha = 0.04f),
    shadow = Color.Black.copy(alpha = 0.30f),
)

val LocalPalette = staticCompositionLocalOf { SagePalette }

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
