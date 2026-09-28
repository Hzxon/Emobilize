package ayodong.emobilize.ui.dock

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
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
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import ayodong.emobilize.model.FabAction
import ayodong.emobilize.model.NavTab
import ayodong.emobilize.model.displayAngle
import ayodong.emobilize.ui.theme.AppMetrics
import ayodong.emobilize.ui.theme.LocalPalette
import ayodong.emobilize.ui.theme.appStyle
import ayodong.emobilize.ui.theme.menuColor
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlinx.coroutines.withTimeoutOrNull

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
    var highlighted by remember { mutableStateOf<FabAction?>(null) }
    val onOpenState = rememberUpdatedState(onMenuOpenChange)
    val menuOpenState = rememberUpdatedState(menuOpen)
    val onActionState = rememberUpdatedState(onAction)
    var widthPx by remember { mutableStateOf(0f) }
    var heightPx by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    val bottomInset = with(density) { WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding().toPx() }
    val arcPx = with(density) { AppMetrics.arcRadius.toPx() }
    val fabPx = with(density) { AppMetrics.fabSize.toPx() }
    val nodePx = with(density) { AppMetrics.nodeSize.toPx() }
    val centerX = widthPx / 2f
    val centerY = heightPx - bottomInset - with(density) { AppMetrics.fabCenterFromBottom.toPx() }
    val actions = FabAction.entries.filter { tab != NavTab.Calendar || it != FabAction.Update }

    LaunchedEffect(menuOpen) {
        if (!menuOpen) highlighted = null
        progress.animateTo(if (menuOpen) 1f else 0f, tween(250))
    }

    Box(
        modifier
            .fillMaxSize()
            .onSizeChanged {
                widthPx = it.width.toFloat()
                heightPx = it.height.toFloat()
            },
    ) {
        val shown = if (dragging && menuOpen) 1f else progress.value
        if (shown > 0.01f) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(palette.overlayBg.copy(alpha = palette.overlayBg.alpha * shown))
                    .then(if (menuOpen) Modifier.clickable { onMenuOpenChange(false) } else Modifier),
            )
        }

        if (widthPx > 0f) {
            Canvas(Modifier.fillMaxSize().graphicsLayer { alpha = shown }) {
                actions.forEach { action ->
                    val angle = displayAngle(action, tab)
                    val color = palette.menuColor(action)
                    val active = highlighted == action
                    val path = sectorPath(
                        center = Offset(centerX, centerY),
                        inner = fabPx / 2f + 6.dp.toPx(),
                        outer = arcPx + 12.dp.toPx(),
                        startDeg = angle - 27f,
                        endDeg = angle + 27f,
                    )
                    drawPath(path, if (active) color else color.copy(alpha = 0.07f))
                    drawPath(
                        path,
                        if (active) color else palette.primary.copy(alpha = 0.21f),
                        style = Stroke(width = if (active) 2.dp.toPx() else 1.dp.toPx()),
                    )
                }
            }

            actions.forEachIndexed { index, action ->
                val angle = displayAngle(action, tab)
                val radians = Math.toRadians(angle.toDouble())
                val reach = arcPx * 0.78f
                val x = centerX + (reach * sin(radians)).toFloat()
                val y = centerY + (-reach * cos(radians)).toFloat()
                val nodeP = ((shown - index * 0.08f) / 0.75f).coerceIn(0f, 1f)
                val color = palette.menuColor(action)
                val active = highlighted == action
                Column(
                    modifier = Modifier
                        .zIndex(2f)
                        .offset {
                            IntOffset((x - nodePx / 2f).roundToInt(), (y - nodePx / 2f).roundToInt())
                        }
                        .size(AppMetrics.nodeSize)
                        .graphicsLayer {
                            alpha = nodeP
                            val scale = 0.2f + nodeP * 0.8f
                            scaleX = scale
                            scaleY = scale
                            translationY = (1f - nodeP) * 20.dp.toPx()
                        }
                        .then(if (menuOpen) Modifier.clickable { onAction(action) } else Modifier),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                ) {
                    Icon(
                        actionIcon(action),
                        contentDescription = action.label,
                        tint = if (active) palette.onDark else color,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        action.label.uppercase(),
                        style = appStyle(7.5.sp, FontWeight.Bold, letterSpacing = 0.05.em),
                        color = if (active) palette.onDark else color,
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
                    .pointerInput(tab) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val slop = 12.dp.toPx()
                        val innerLimit = 36.dp.toPx()
                        val outerLimit = arcPx + nodePx
                        fun nearest(position: Offset): FabAction? {
                            val dx = position.x - center.x
                            val dy = position.y - center.y
                            val distance = hypot(dx, dy)
                            if (distance < innerLimit || distance > outerLimit) return null
                            val pointerAngle = Math.toDegrees(atan2(dx.toDouble(), (-dy).toDouble())).toFloat()
                            return actions
                                .map { action ->
                                    val raw = abs(pointerAngle - displayAngle(action, tab)) % 360f
                                    action to min(raw, 360f - raw)
                                }
                                .minBy { it.second }
                                .takeIf { it.second <= 30f }
                                ?.first
                        }
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            dragging = true
                            highlighted = null
                            var moved = false
                            var openedByGesture = false
                            var opened = menuOpenState.value
                            val pointerId = down.id
                            val start = down.position
                            val releasedDuringHold = withTimeoutOrNull(220L) {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.firstOrNull { it.id == pointerId }
                                        ?: return@withTimeoutOrNull true
                                    if (!change.pressed) return@withTimeoutOrNull true
                                    val delta = change.position - start
                                    if (hypot(delta.x, delta.y) > slop) {
                                        moved = true
                                        change.consume()
                                        highlighted = nearest(change.position)
                                        return@withTimeoutOrNull false
                                    }
                                }
                            }
                            if (releasedDuringHold == true && !moved) {
                                dragging = false
                                highlighted = null
                                onOpenState.value(!menuOpenState.value)
                                return@awaitEachGesture
                            }
                            openedByGesture = true
                            if (!opened) {
                                opened = true
                                onOpenState.value(true)
                            }
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.id == pointerId } ?: break
                                if (!change.pressed) {
                                    val selected = highlighted
                                    dragging = false
                                    highlighted = null
                                    if (selected != null) onActionState.value(selected)
                                    else if (!moved && !openedByGesture) onOpenState.value(!opened)
                                    else onOpenState.value(false)
                                    break
                                }
                                change.consume()
                                val delta = change.position - start
                                if (hypot(delta.x, delta.y) > slop) moved = true
                                highlighted = nearest(change.position)
                            }
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                FabFace(progress = shown, showCancel = dragging && menuOpen)
            }
        }
    }
}

private fun actionIcon(action: FabAction): ImageVector = when (action) {
    FabAction.Add -> Icons.Filled.Add
    FabAction.Update -> Icons.Filled.Refresh
    FabAction.Edit -> Icons.Filled.Edit
    FabAction.Delete -> Icons.Filled.Delete
}

private fun sectorPath(center: Offset, inner: Float, outer: Float, startDeg: Float, endDeg: Float): Path {
    fun point(radius: Float, degrees: Float): Offset {
        val radians = Math.toRadians(degrees.toDouble())
        return Offset(
            center.x + radius * sin(radians).toFloat(),
            center.y - radius * cos(radians).toFloat(),
        )
    }
    return Path().apply {
        moveTo(point(outer, startDeg).x, point(outer, startDeg).y)
        arcTo(
            rect = Rect(center.x - outer, center.y - outer, center.x + outer, center.y + outer),
            startAngleDegrees = startDeg - 90f,
            sweepAngleDegrees = endDeg - startDeg,
            forceMoveTo = false,
        )
        lineTo(point(inner, endDeg).x, point(inner, endDeg).y)
        arcTo(
            rect = Rect(center.x - inner, center.y - inner, center.x + inner, center.y + inner),
            startAngleDegrees = endDeg - 90f,
            sweepAngleDegrees = startDeg - endDeg,
            forceMoveTo = false,
        )
        close()
    }
}
