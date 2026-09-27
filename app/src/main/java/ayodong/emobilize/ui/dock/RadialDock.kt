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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.automirrored.filled.List
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
import ayodong.emobilize.model.FilterKey
import ayodong.emobilize.model.NavTab
import ayodong.emobilize.model.displayAngle
import ayodong.emobilize.ui.theme.AppMetrics
import ayodong.emobilize.ui.theme.HoloPalette
import ayodong.emobilize.ui.theme.LocalPalette
import ayodong.emobilize.ui.theme.SagePalette
import ayodong.emobilize.ui.theme.appStyle
import ayodong.emobilize.ui.theme.at
import ayodong.emobilize.ui.theme.inkOn
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlinx.coroutines.withTimeoutOrNull


private enum class MenuMode { Actions, Filters }

private data class RadialNode(
    val key: String,
    val label: String,
    val angle: Float,
    val color: Color,
    val onClick: () -> Unit,
)

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
fun RadialMenu(
    tab: NavTab,
    menuOpen: Boolean,
    onMenuOpenChange: (Boolean) -> Unit,
    onAction: (FabAction) -> Unit,
    onFilter: (FilterKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalPalette.current
    val haptic = LocalHapticFeedback.current
    val progress = remember { Animatable(0f) }
    var dragging by remember { mutableStateOf(false) }
    var gestureProgress by remember { mutableFloatStateOf(0f) }
    var menuMode by remember { mutableStateOf(MenuMode.Actions) }
    val onOpenState = rememberUpdatedState(onMenuOpenChange)
    val menuOpenState = rememberUpdatedState(menuOpen)
    val onActionState = rememberUpdatedState(onAction)
    val onFilterState = rememberUpdatedState(onFilter)
    var widthPx by remember { mutableStateOf(0f) }
    var heightPx by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    val bottomInset = with(density) { WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding().toPx() }
    val arcPx = with(density) { AppMetrics.arcRadius.toPx() }
    val fabPx = with(density) { AppMetrics.fabSize.toPx() }
    val nodePx = with(density) { AppMetrics.nodeSize.toPx() }
    val centerX = widthPx / 2f
    val centerY = heightPx - bottomInset - with(density) { AppMetrics.fabCenterFromBottom.toPx() }
    val nodes = radialNodes(
        mode = menuMode,
        tab = tab,
        palette = palette,
        onAction = onActionState.value,
        onFilter = onFilterState.value,
    )

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
                nodes.forEach { node ->
                    val theta = Math.toRadians(node.angle.toDouble())
                    val dx = (arcPx * sin(theta)).toFloat()
                    val dy = (-arcPx * cos(theta)).toFloat()
                    val color = node.color.copy(alpha = 0.55f * shown)
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

            nodes.forEachIndexed { index, node ->
                val theta = Math.toRadians(node.angle.toDouble())
                val x = centerX + (arcPx * sin(theta)).toFloat()
                val y = centerY + (-arcPx * cos(theta)).toFloat()
                val nodeP = ((shown - index * 0.08f) / 0.75f).coerceIn(0f, 1f)
                val ink = palette.inkOn(node.color)
                Box(
                    modifier = Modifier
                        .zIndex(2f)
                        .offset {
                            IntOffset(
                                (x - nodePx / 2f).roundToInt(),
                                (y - nodePx / 2f).roundToInt(),
                            )
                        }
                        .size(AppMetrics.nodeSize)
                        .graphicsLayer {
                            alpha = nodeP
                            val scale = 0.2f + nodeP * 0.8f
                            scaleX = scale
                            scaleY = scale
                            translationY = (1f - nodeP) * 20.dp.toPx()
                        }
                        .clip(CircleShape)
                        .background(node.color)
                        .then(
                            if (menuOpen) {
                                Modifier.clickable { node.onClick() }
                            } else {
                                Modifier
                            },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = node.label,
                        style = appStyle(if (node.label.length > 6) 7.sp else 9.sp, FontWeight.Bold, letterSpacing = 0.04.em),
                        color = ink,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
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
                    .size(AppMetrics.fabSize)
                    .graphicsLayer {
                        val scale = 1f + shown * 0.07f
                        scaleX = scale
                        scaleY = scale
                        translationY = -shown * 6.dp.toPx()
                    }
                    .pointerInput(Unit) {
                        val swipe = 90.dp.toPx()
                        val slop = viewConfiguration.touchSlop
                        awaitEachGesture {
                            val down = awaitFirstDown()
                            val pointerId = down.id
                            var pulled = 0f
                            var swiping = false
                            val earlyRelease = withTimeoutOrNull(500L) {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.firstOrNull { it.id == pointerId } ?: return@withTimeoutOrNull true
                                    if (!change.pressed) return@withTimeoutOrNull true
                                    pulled += change.previousPosition.y - change.position.y
                                    if (abs(pulled) > slop) {
                                        swiping = true
                                        change.consume()
                                        return@withTimeoutOrNull false
                                    }
                                }
                            }
                            when {
                                earlyRelease == true -> {
                                    if (menuOpenState.value) onOpenState.value(false)
                                    dragging = false
                                }
                                !swiping -> {
                                    menuMode = MenuMode.Filters
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    if (!menuOpenState.value) {
                                        gestureProgress = 0f
                                        dragging = false
                                        onOpenState.value(true)
                                    }
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        val change = event.changes.firstOrNull { it.id == pointerId } ?: break
                                        if (!change.pressed) break
                                        change.consume()
                                    }
                                }
                                else -> {
                                    menuMode = MenuMode.Actions
                                    dragging = true
                                    gestureProgress = (pulled / swipe).coerceIn(0f, 1f)
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        val change = event.changes.firstOrNull { it.id == pointerId } ?: break
                                        if (!change.pressed) break
                                        pulled += change.previousPosition.y - change.position.y
                                        change.consume()
                                        gestureProgress = (pulled / swipe).coerceIn(0f, 1f)
                                    }
                                    onOpenState.value(gestureProgress >= 0.4f)
                                    dragging = false
                                }
                            }
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
    val swatch = if (themeId == 1) HoloPalette.ring else listOf(SagePalette.primary, SagePalette.primary)
    Box(
        modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(Brush.sweepGradient(swatch))
            .clickable(onClick = onClick),
    )
}

private fun actionColor(action: FabAction, palette: ayodong.emobilize.ui.theme.AppPalette): Color = when (action) {
    FabAction.Add -> palette.actionAdd
    FabAction.Update -> palette.actionUpdate
    FabAction.Edit -> palette.actionEdit
    FabAction.Delete -> palette.danger
}

private fun radialNodes(
    mode: MenuMode,
    tab: NavTab,
    palette: ayodong.emobilize.ui.theme.AppPalette,
    onAction: (FabAction) -> Unit,
    onFilter: (FilterKey) -> Unit,
): List<RadialNode> {
    if (mode == MenuMode.Filters) {
        val filters = listOf(
            Triple(FilterKey.All, "ALL", -87f),
            Triple(FilterKey.Today, "TODAY", -30f),
            Triple(FilterKey.Tomorrow, "TOMORROW", 30f),
            Triple(FilterKey.Later, "LATER", 87f),
        )
        val colors = listOf(palette.primary, palette.statusProgress, palette.actionUpdate, palette.catPersonal)
        return filters.mapIndexed { index, (key, label, angle) ->
            RadialNode(
                key = key.name,
                label = label,
                angle = angle,
                color = colors[index],
                onClick = { onFilter(key) },
            )
        }
    }
    return FabAction.entries.mapNotNull { action ->
        if (action == FabAction.Update && tab == NavTab.Calendar) return@mapNotNull null
        RadialNode(
            key = action.name,
            label = action.label.uppercase(),
            angle = displayAngle(action, tab),
            color = actionColor(action, palette),
            onClick = { onAction(action) },
        )
    }
}
