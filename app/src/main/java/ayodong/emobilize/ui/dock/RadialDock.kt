package ayodong.emobilize.ui.dock

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import ayodong.emobilize.model.FabAction
import ayodong.emobilize.model.NavTab
import ayodong.emobilize.model.displayAngle
import ayodong.emobilize.ui.theme.AppMetrics
import ayodong.emobilize.ui.theme.LocalPalette
import ayodong.emobilize.ui.theme.NeutralDark
import ayodong.emobilize.ui.theme.NeutralLight
import ayodong.emobilize.ui.theme.appStyle
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlinx.coroutines.withTimeoutOrNull


@Composable
fun BottomDock(
    tab: NavTab,
    onTab: (NavTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalPalette.current
    Row(
        modifier
            .fillMaxWidth()
            .background(palette.navBg)
            .navigationBarsPadding()
            .height(AppMetrics.navHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DockTab(
            label = "TASKS",
            selected = tab == NavTab.Tasks,
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(22.dp)) },
            onClick = { onTab(NavTab.Tasks) },
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(AppMetrics.fabSize + 8.dp))
        DockTab(
            label = "CALENDAR",
            selected = tab == NavTab.Calendar,
            icon = { Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier.size(22.dp)) },
            onClick = { onTab(NavTab.Calendar) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DockTab(
    label: String,
    selected: Boolean,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    val palette = LocalPalette.current
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(Modifier.graphicsLayer { alpha = 1f }, contentAlignment = Alignment.Center) {
            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.material3.LocalContentColor provides if (selected) palette.text else palette.textMuted,
            ) {
                icon()
            }
        }
        Text(
            text = label,
            style = appStyle(9.5.sp, FontWeight.Bold, letterSpacing = 0.1.em),
            color = if (selected) palette.text else palette.textMuted,
        )
        Box(
            Modifier
                .width(18.dp)
                .height(2.dp)
                .clip(CircleShape)
                .background(if (selected) palette.primary else Color.Transparent),
        )
    }
}

@Composable
internal fun FabFace(progress: Float, showCancel: Boolean) {
    val palette = LocalPalette.current
    val float = rememberInfiniteTransition(label = "cube")
    val lift by float.animateFloat(
        initialValue = 0f,
        targetValue = -2f,
        animationSpec = infiniteRepeatable(tween(1300), RepeatMode.Reverse),
        label = "lift",
    )
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer { rotationZ = -90f }
                .background(Brush.sweepGradient(palette.ring), CircleShape),
        )
        Box(
            Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = palette.cubeInner,
                        center = Offset(40f, 40f),
                        radius = 90f,
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (palette.cubeGlow.alpha > 0f) {
                Box(
                    Modifier
                        .size(46.dp)
                        .background(
                            Brush.radialGradient(listOf(palette.cubeGlow, Color.Transparent)),
                            CircleShape,
                        ),
                )
            }
            HoloCube(
                modifier = Modifier
                    .size(42.dp, 46.dp)
                    .graphicsLayer {
                        translationY = lift.dp.toPx()
                        alpha = if (showCancel) 0f else 1f
                    },
            )
            Column(
                Modifier.graphicsLayer { alpha = if (showCancel) 1f else 0f },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Icon(Icons.Filled.Close, contentDescription = null, tint = palette.text, modifier = Modifier.size(24.dp))
                Text("BATAL", style = appStyle(7.sp, FontWeight.Bold, letterSpacing = 0.08.em), color = palette.text)
            }
        }
    }
}

@Composable
fun HoloCube(modifier: Modifier = Modifier) {
    val palette = LocalPalette.current
    Canvas(modifier) {
        fun at(x: Float, y: Float) = Offset(x / 42f * size.width, y / 46f * size.height)
        fun poly(a: Offset, b: Offset, c: Offset, d: Offset) = Path().apply {
            moveTo(a.x, a.y)
            lineTo(b.x, b.y)
            lineTo(c.x, c.y)
            lineTo(d.x, d.y)
            close()
        }
        val top = poly(at(21f, 4f), at(39f, 14f), at(21f, 24f), at(3f, 14f))
        val left = poly(at(3f, 14f), at(21f, 24f), at(21f, 42f), at(3f, 32f))
        val right = poly(at(21f, 24f), at(39f, 14f), at(39f, 32f), at(21f, 42f))
        drawPath(top, Brush.linearGradient(palette.cubeTop))
        drawPath(left, Brush.linearGradient(palette.cubeLeft))
        drawPath(right, Brush.linearGradient(palette.cubeRight))
        val neon = palette.id == 2
        drawPath(top, palette.cubeShine.copy(alpha = if (neon) 0.55f else 0.28f))
        val edgeWidth = if (neon) 1.3.dp.toPx() else 0.8.dp.toPx()
        val edge = Stroke(width = edgeWidth)
        drawPath(top, palette.cubeEdge.copy(alpha = if (neon) 0.95f else 0.33f), style = edge)
        drawLine(
            palette.cubeEdge.copy(alpha = if (neon) 0.8f else 0.25f),
            at(21f, 24f),
            at(21f, 42f),
            strokeWidth = edgeWidth,
        )
    }
}

@Composable
fun ThemeOrb(
    themeId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val swatch = if (themeId == 1) NeutralDark.ring else listOf(NeutralLight.primary, NeutralLight.primary)
    Box(
        modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(Brush.sweepGradient(swatch))
            .clickable(onClick = onClick),
    )
}
