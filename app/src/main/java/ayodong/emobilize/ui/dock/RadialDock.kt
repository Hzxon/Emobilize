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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.automirrored.filled.List
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
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
import ayodong.emobilize.ui.theme.LocalPalette
import ayodong.emobilize.ui.theme.appStyle
import ayodong.emobilize.ui.theme.at
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlinx.coroutines.launch

private const val ArcRadius = 120f
private const val FabSize = 88f
private const val NodeSize = 48f

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
            .height(88.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DockTab(
            label = "TASKS",
            selected = tab == NavTab.Tasks,
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(22.dp)) },
            onClick = { onTab(NavTab.Tasks) },
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width((FabSize + 8).dp))
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
fun RadialMenu(
    tab: NavTab,
    menuOpen: Boolean,
    onMenuOpenChange: (Boolean) -> Unit,
    onAction: (FabAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalPalette.current
    val progress = remember { Animatable(0f) }
    var dragging by remember { mutableStateOf(false) }
    var gestureProgress by remember { mutableFloatStateOf(0f) }
    val onOpenState = rememberUpdatedState(onMenuOpenChange)
    var widthPx by remember { mutableStateOf(0f) }
    var heightPx by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    val bottomInset = with(density) { WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding().toPx() }
    val arcPx = with(density) { ArcRadius.dp.toPx() }
    val fabPx = with(density) { FabSize.dp.toPx() }
    val nodePx = with(density) { NodeSize.dp.toPx() }
    val centerX = widthPx / 2f
    val centerY = heightPx - bottomInset - with(density) { 52.dp.toPx() }

    LaunchedEffect(menuOpen, dragging) {
        if (!dragging) {
            progress.snapTo(gestureProgress)
            progress.animateTo(
                if (menuOpen) 1f else 0f,
                tween(durationMillis = 280, easing = FastOutSlowInEasing),
            )
        }
    }

    Box(
        modifier
            .fillMaxSize()
            .onSizeChanged {
                widthPx = it.width.toFloat()
                heightPx = it.height.toFloat()
            },
    ) {
        val shown = if (dragging) gestureProgress else progress.value
        if (shown > 0.01f) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(palette.overlayBg.copy(alpha = palette.overlayBg.alpha * shown))
                    .then(
                        if (menuOpen) {
                            Modifier.clickable { onMenuOpenChange(false) }
                        } else {
                            Modifier
                        },
                    ),
            )
        }

        if (widthPx > 0f) {
            Canvas(Modifier.fillMaxSize()) {
                val dash = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 5.dp.toPx()))
                drawArc(
                    color = palette.primary.copy(alpha = 0.31f * shown),
                    startAngle = -177f,
                    sweepAngle = -174f,
                    useCenter = false,
                    topLeft = Offset(centerX - arcPx, centerY - arcPx),
                    size = Size(arcPx * 2, arcPx * 2),
                    style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, pathEffect = dash),
                )
                FabAction.entries.forEach { action ->
                    if (action == FabAction.Update && tab == NavTab.Calendar) return@forEach
                    val theta = Math.toRadians(displayAngle(action, tab).toDouble())
                    val dx = (arcPx * sin(theta)).toFloat()
                    val dy = (-arcPx * cos(theta)).toFloat()
                    val color = actionColor(action, palette).copy(alpha = 0.55f * shown)
                    drawLine(
                        color = color,
                        start = Offset(centerX + dx * 0.52f, centerY + dy * 0.52f),
                        end = Offset(centerX + dx * 0.70f, centerY + dy * 0.70f),
                        strokeWidth = 1.5.dp.toPx(),
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx())),
                    )
                    drawCircle(color, radius = 2.5.dp.toPx(), center = Offset(centerX + dx * 0.76f, centerY + dy * 0.76f))
                }
            }

            FabAction.entries.forEachIndexed { index, action ->
                if (action == FabAction.Update && tab == NavTab.Calendar) return@forEachIndexed
                val theta = Math.toRadians(displayAngle(action, tab).toDouble())
                val x = centerX + (arcPx * sin(theta)).toFloat()
                val y = centerY + (-arcPx * cos(theta)).toFloat()
                val nodeP = ((shown - index * 0.08f) / 0.75f).coerceIn(0f, 1f)
                val color = actionColor(action, palette)
                Box(
                    modifier = Modifier
                        .zIndex(2f)
                        .offset {
                            IntOffset(
                                (x - nodePx / 2f).roundToInt(),
                                (y - nodePx / 2f).roundToInt(),
                            )
                        }
                        .size(NodeSize.dp)
                        .graphicsLayer {
                            alpha = nodeP
                            val scale = 0.2f + nodeP * 0.8f
                            scaleX = scale
                            scaleY = scale
                            translationY = (1f - nodeP) * 20.dp.toPx()
                        }
                        .clip(CircleShape)
                        .background(color)
                        .then(
                            if (menuOpen) {
                                Modifier.clickable { onAction(action) }
                            } else {
                                Modifier
                            },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(actionIcon(action), contentDescription = action.label, tint = Color.White, modifier = Modifier.size(16.dp))
                        Text(
                            text = action.label.uppercase(),
                            style = appStyle(7.5.sp, FontWeight.Bold, letterSpacing = 0.05.em),
                            color = Color.White,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }

        val bob = rememberInfiniteTransition(label = "swipe")
        val bobY by bob.animateFloat(
            initialValue = 0f,
            targetValue = -5f,
            animationSpec = infiniteRepeatable(tween(750), RepeatMode.Reverse),
            label = "bob",
        )
        if (shown < 0.2f && widthPx > 0f) {
            Icon(
                Icons.Filled.KeyboardArrowUp,
                contentDescription = null,
                tint = palette.primaryDark,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (centerX - 9.dp.toPx()).roundToInt(),
                            (centerY - fabPx / 2f - 26.dp.toPx() + bobY.dp.toPx()).roundToInt(),
                        )
                    }
                    .size(18.dp)
                    .graphicsLayer { alpha = if (shown < 0.05f) 0.65f else 0f },
            )
        }

        if (widthPx > 0f) {
            Box(
                modifier = Modifier
                    .zIndex(3f)
                    .offset {
                        IntOffset(
                            (centerX - fabPx / 2f).roundToInt(),
                            (centerY - fabPx / 2f).roundToInt(),
                        )
                    }
                    .size(FabSize.dp)
                    .graphicsLayer {
                        val scale = 1f + shown * 0.07f
                        scaleX = scale
                        scaleY = scale
                        translationY = -shown * 6.dp.toPx()
                    }
                    .pointerInput(Unit) {
                        val swipe = 90.dp.toPx()
                        awaitEachGesture {
                            val down = awaitFirstDown()
                            gestureProgress = 0f
                            dragging = true
                            var pulled = 0f
                            val pointerId = down.id
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.id == pointerId } ?: break
                                if (!change.pressed) break
                                pulled += change.previousPosition.y - change.position.y
                                change.consume()
                                gestureProgress = (pulled / swipe).coerceIn(0f, 1f)
                            }
                            val open = gestureProgress >= 0.4f
                            onOpenState.value(open)
                            dragging = false
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                FabFace(progress = shown)
            }
        }
    }
}

@Composable
private fun FabFace(progress: Float) {
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
            HoloCube(
                modifier = Modifier
                    .size(42.dp, 46.dp)
                    .graphicsLayer { translationY = lift.dp.toPx() },
            )
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
        drawPath(top, palette.cubeShine.copy(alpha = 0.28f))
        val edge = Stroke(width = 0.8.dp.toPx())
        drawPath(top, palette.cubeEdge.copy(alpha = 0.33f), style = edge)
        drawLine(palette.cubeEdge.copy(alpha = 0.25f), at(21f, 24f), at(21f, 42f), strokeWidth = 0.8.dp.toPx())
    }
}

@Composable
fun ThemeOrb(
    themeId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = listOf(
        Color(0xFF0099CC), Color(0xFF00D4FF), Color(0xFF39FF7A),
        Color(0xFFC678FF), Color(0xFFFF7B35), Color(0xFF0099CC),
    )
    Box(
        modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(if (themeId == 1) Brush.sweepGradient(colors) else Brush.linearGradient(listOf(Color(0xFF8B9A6E), Color(0xFF8B9A6E))))
            .clickable(onClick = onClick),
    )
}

private fun actionColor(action: FabAction, palette: ayodong.emobilize.ui.theme.AppPalette): Color = when (action) {
    FabAction.Add -> palette.actionAdd
    FabAction.Update -> palette.actionUpdate
    FabAction.Edit -> palette.actionEdit
    FabAction.Delete -> Color(0xFFEF4444)
}

private fun actionIcon(action: FabAction) = when (action) {
    FabAction.Add -> Icons.Filled.Add
    FabAction.Update -> Icons.Filled.Refresh
    FabAction.Edit -> Icons.Filled.Edit
    FabAction.Delete -> Icons.Filled.Delete
}
