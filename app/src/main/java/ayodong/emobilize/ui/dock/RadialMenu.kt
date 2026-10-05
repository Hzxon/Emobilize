package ayodong.emobilize.ui.dock

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
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

@Composable
fun RadialMenu(
    tab: NavTab,
    menuOpen: Boolean,
    onMenuOpenChange: (Boolean) -> Unit,
    onAction: (FabAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalPalette.current
    val progress by animateFloatAsState(
        targetValue = if (menuOpen) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow),
        label = "radialProgress",
    )
    var dragging by remember { mutableStateOf(false) }
    var highlighted by remember { mutableStateOf<FabAction?>(null) }
    val onOpenState = rememberUpdatedState(onMenuOpenChange)
    val menuOpenState = rememberUpdatedState(menuOpen)
    val onActionState = rememberUpdatedState(onAction)
    val progressState = rememberUpdatedState(progress)
    var widthPx by remember { mutableFloatStateOf(0f) }
    var heightPx by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current
    val haptics = LocalHapticFeedback.current
    val bottomInset = with(density) { WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding().toPx() }
    val arcPx = with(density) { AppMetrics.arcRadius.toPx() }
    val fabPx = with(density) { AppMetrics.fabSize.toPx() }
    val nodePx = with(density) { AppMetrics.nodeSize.toPx() }
    val wheelPx = with(density) { (AppMetrics.arcRadius + AppMetrics.nodeSize).toPx() * 2f }
    val restFromBottom = with(density) { AppMetrics.fabCenterFromBottom.toPx() }
    val hubFromBottom = with(density) { (16.dp + AppMetrics.arcRadius).toPx() }
    val travelPx = hubFromBottom - restFromBottom
    val innerLimit = fabPx / 2f + with(density) { 8.dp.toPx() }
    val outerLimit = arcPx + nodePx
    val centerX = widthPx / 2f
    val restY = heightPx - bottomInset - restFromBottom
    val hubY = heightPx - bottomInset - hubFromBottom
    val actions = FabAction.entries.filter { tab != NavTab.Calendar || it != FabAction.Update }

    LaunchedEffect(menuOpen) {
        if (!menuOpen) {
            highlighted = null
            dragging = false
        }
    }

    fun nearest(position: Offset, origin: Offset): FabAction? {
        val dx = position.x - origin.x
        val dy = position.y - origin.y
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

    Box(
        modifier
            .fillMaxSize()
            .onSizeChanged {
                widthPx = it.width.toFloat()
                heightPx = it.height.toFloat()
            },
    ) {
        if (progress > 0.01f) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(palette.overlayBg.copy(alpha = palette.overlayBg.alpha * progress.coerceIn(0f, 1f)))
                    .then(
                        if (menuOpen) {
                            Modifier.pointerInput(tab, arcPx, travelPx) {
                                detectTapGestures { offset ->
                                    val hub = Offset(size.width / 2f, size.height - bottomInset - hubFromBottom)
                                    val selected = nearest(offset, hub)
                                    onOpenState.value(false)
                                    if (selected != null) onActionState.value(selected)
                                }
                            }
                        } else {
                            Modifier
                        },
                    ),
            )
        }

        if (progress > 0.01f && widthPx > 0f) {
            Box(
                Modifier
                    .zIndex(1f)
                    .offset {
                        IntOffset(
                            (centerX - wheelPx / 2f).roundToInt(),
                            (hubY - wheelPx / 2f).roundToInt(),
                        )
                    }
                    .size(with(density) { wheelPx.toDp() })
                    .graphicsLayer {
                        val scale = progress.coerceAtLeast(0f)
                        scaleX = scale
                        scaleY = scale
                        alpha = progress.coerceIn(0f, 1f)
                    },
            ) {
                Canvas(Modifier.fillMaxSize()) {
                    actions.forEach { action ->
                        val angle = displayAngle(action, tab)
                        val color = palette.menuColor(action)
                        val active = highlighted == action
                        val path = sectorPath(
                            center = Offset(size.width / 2f, size.height / 2f),
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

                actions.forEach { action ->
                    val angle = displayAngle(action, tab)
                    val radians = Math.toRadians(angle.toDouble())
                    val reach = arcPx * 0.78f
                    val x = wheelPx / 2f + (reach * sin(radians)).toFloat()
                    val y = wheelPx / 2f + (-reach * cos(radians)).toFloat()
                    val color = palette.menuColor(action)
                    val active = highlighted == action
                    Column(
                        modifier = Modifier
                            .offset {
                                IntOffset((x - nodePx / 2f).roundToInt(), (y - nodePx / 2f).roundToInt())
                            }
                            .size(AppMetrics.nodeSize)
                            .semantics {
                                contentDescription = action.label
                                onClick {
                                    onActionState.value(action)
                                    true
                                }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            actionIcon(action),
                            contentDescription = null,
                            tint = if (active) palette.onDark else color,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            action.label,
                            style = appStyle(10.sp, FontWeight.Medium),
                            color = if (active) palette.onDark else color,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                        )
                    }
                }
            }
        }

        if (progress < 0.05f && widthPx > 0f) {
            Icon(
                Icons.Filled.KeyboardArrowUp,
                contentDescription = null,
                tint = palette.textMuted,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (centerX - 9.dp.toPx()).roundToInt(),
                            (restY - fabPx / 2f - 26.dp.toPx()).roundToInt(),
                        )
                    }
                    .size(18.dp),
            )
        }

        if (widthPx > 0f) {
            Box(
                modifier = Modifier
                    .zIndex(3f)
                    .offset {
                        IntOffset(
                            (centerX - fabPx / 2f).roundToInt(),
                            (restY - fabPx / 2f).roundToInt(),
                        )
                    }
                    .size(AppMetrics.fabSize)
                    .graphicsLayer { translationY = -travelPx * progress },
                contentAlignment = Alignment.Center,
            ) {
                FabFace(progress = progress, showCancel = dragging && menuOpen)
            }

            Box(
                modifier = Modifier
                    .zIndex(4f)
                    .offset {
                        IntOffset(
                            (centerX - fabPx / 2f).roundToInt(),
                            (restY - fabPx / 2f).roundToInt(),
                        )
                    }
                    .size(AppMetrics.fabSize)
                    .semantics {
                        contentDescription = if (menuOpen) "Close menu" else "Open menu"
                        onClick {
                            onOpenState.value(!menuOpenState.value)
                            true
                        }
                    }
                    .pointerInput(tab) {
                        val origin = Offset(size.width / 2f, size.height / 2f - travelPx)
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val wasOpen = menuOpenState.value
                            if (!wasOpen) onOpenState.value(true)
                            dragging = true
                            down.consume()

                            fun updateHover(position: Offset) {
                                val next = if (progressState.value < 0.5f) {
                                    null
                                } else {
                                    nearest(position, origin)
                                }
                                if (next != highlighted) {
                                    highlighted = next
                                    if (next != null) {
                                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                }
                            }

                            updateHover(down.position)
                            var moved = false
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                if ((change.position - down.position).getDistance() > viewConfiguration.touchSlop) {
                                    moved = true
                                }
                                updateHover(change.position)
                                change.consume()
                                if (!change.pressed) break
                            }

                            val selected = highlighted
                            highlighted = null
                            dragging = false
                            when {
                                selected != null -> {
                                    onOpenState.value(false)
                                    onActionState.value(selected)
                                }
                                moved -> onOpenState.value(false)
                                wasOpen -> onOpenState.value(false)
                                else -> Unit
                            }
                        }
                    },
            )
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
