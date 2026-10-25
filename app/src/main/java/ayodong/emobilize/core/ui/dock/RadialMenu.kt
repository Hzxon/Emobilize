package ayodong.emobilize.core.ui.dock

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import ayodong.emobilize.core.ui.theme.monoStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.border
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
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import ayodong.emobilize.core.ui.theme.AppMetrics
import ayodong.emobilize.core.ui.theme.LocalPalette
import ayodong.emobilize.core.ui.theme.appStyle
import ayodong.emobilize.core.ui.theme.menuColor
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun RadialMenu(
    tab: NavTab,
    menuOpen: Boolean,
    onMenuOpenChange: (Boolean) -> Unit,
    onAction: (FabAction) -> Unit,
    // A quick tap on the key switches between Departures and Route map; hold or drag opens the fan.
    onSwitchTab: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalPalette.current
    val onSwitchState = rememberUpdatedState(onSwitchTab)
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
    val innerLimit = with(density) { DialInner.toPx() - 6.dp.toPx() }
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

    val slices = dialSlices(actions, tab)
    val needleAngle by animateFloatAsState(
        targetValue = slices.firstOrNull { it.action == highlighted }?.center ?: 0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
        label = "dialNeedle",
    )

    // The slice under the finger, or null inside the hub or past the dial's rim.
    fun nearest(position: Offset, origin: Offset): FabAction? {
        val dx = position.x - origin.x
        val dy = position.y - origin.y
        val distance = hypot(dx, dy)
        if (distance < innerLimit || distance > outerLimit) return null
        val pointerAngle = Math.toDegrees(atan2(dx.toDouble(), (-dy).toDouble())).toFloat()
        return slices.firstOrNull { pointerAngle >= it.start && pointerAngle < it.end }?.action
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
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val inner = DialInner.toPx()
                    val outer = DialOuter.toPx()
                    val rim = DialRim.toPx()
                    fun at(radius: Float, degrees: Float): Offset {
                        val radians = Math.toRadians(degrees.toDouble())
                        return Offset(
                            center.x + radius * sin(radians).toFloat(),
                            center.y - radius * cos(radians).toFloat(),
                        )
                    }
                    // Tick ring: a mark every 6 degrees, longer and brighter every 30.
                    for (deg in -120..120 step 6) {
                        val major = deg % 30 == 0
                        drawLine(
                            Color.White.copy(alpha = if (major) 0.8f else 0.35f),
                            at(156.dp.toPx(), deg.toFloat()),
                            at((if (major) 168 else 162).dp.toPx(), deg.toFloat()),
                            strokeWidth = (if (major) 2.5f else 1.5f).dp.toPx(),
                        )
                    }
                    // Slices: dark plates with an action-coloured rim. The picked one fills with
                    // its colour, grows past the rim and takes a signal-yellow rim.
                    slices.forEach { slice ->
                        val on = highlighted == slice.action
                        val fade = if (highlighted != null && !on) 0.6f else 1f
                        val color = dialColor(slice.action)
                        val reach = if (on) outer + DialLift.toPx() else outer
                        drawPath(sectorPath(center, inner, reach, slice.start, slice.end), if (on) color else DialPlate, alpha = fade)
                        drawPath(sectorPath(center, reach - rim, reach, slice.start, slice.end), if (on) palette.signal else color, alpha = fade)
                    }
                    slices.drop(1).forEach { slice ->
                        drawLine(DialSeam, at(inner, slice.start), at(outer - rim, slice.start), strokeWidth = 2.dp.toPx())
                    }
                    // Needle from the hub to the picked slice; it rests pointing straight up.
                    val tip = at(150.dp.toPx(), needleAngle)
                    drawLine(palette.signal, at(40.dp.toPx(), needleAngle), tip, strokeWidth = 4.dp.toPx())
                    drawPath(
                        Path().apply {
                            moveTo(tip.x, tip.y)
                            at(141.dp.toPx(), needleAngle - 3f).let { lineTo(it.x, it.y) }
                            at(141.dp.toPx(), needleAngle + 3f).let { lineTo(it.x, it.y) }
                            close()
                        },
                        palette.signal,
                    )
                    drawCircle(DialSeam, radius = 46.dp.toPx(), center = center)
                    drawCircle(DialHubRing, radius = 46.dp.toPx(), center = center, style = Stroke(width = 2.dp.toPx()))
                }

                // Destination sign above the dial: a prompt until a slice is picked, then its name.
                val picked = highlighted
                Box(
                    Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-42).dp)
                        .background(if (picked != null) palette.signal else palette.signalInk)
                        .border(2.dp, palette.signal)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text(
                        if (picked != null) "\u2192 ${picked.label.uppercase()}" else "SLIDE TO SELECT",
                        style = monoStyle(13.sp, letterSpacing = 1.5.sp),
                        color = if (picked != null) palette.signalInk else palette.signal,
                        maxLines = 1,
                    )
                }

                slices.forEach { slice ->
                    val action = slice.action
                    val on = highlighted == action
                    val radians = Math.toRadians(slice.center.toDouble())
                    val reach = with(density) { (if (on) 101.dp else 95.dp).toPx() }
                    val x = wheelPx / 2f + (reach * sin(radians)).toFloat()
                    val y = wheelPx / 2f + (-reach * cos(radians)).toFloat()
                    val color = dialColor(action)
                    Column(
                        modifier = Modifier
                            .offset {
                                IntOffset((x - nodePx / 2f).roundToInt(), (y - nodePx / 2f).roundToInt())
                            }
                            .size(AppMetrics.nodeSize)
                            .alpha(if (highlighted != null && !on) 0.6f else 1f)
                            .semantics {
                                contentDescription = action.label
                                onClick {
                                    onActionState.value(action)
                                    true
                                }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
                    ) {
                        Icon(
                            actionIcon(action),
                            contentDescription = null,
                            tint = if (on) Color.White else color,
                            modifier = Modifier.size(20.dp),
                        )
                        Text(
                            action.label.uppercase(),
                            style = monoStyle(11.sp, letterSpacing = 1.sp),
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            softWrap = false,
                        )
                    }
                }
            }
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
                FabFace(progress = progress, open = menuOpen, tab = tab)
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
                        contentDescription = when {
                            menuOpen -> "Close actions"
                            tab == NavTab.Tasks -> "Go to Route map"
                            else -> "Go to Departures"
                        }
                        onClick {
                            if (menuOpenState.value) onOpenState.value(false) else onSwitchState.value()
                            true
                        }
                        onLongClick(label = "Open actions") {
                            onOpenState.value(true)
                            true
                        }
                    }
                    .pointerInput(tab) {
                        val origin = Offset(size.width / 2f, size.height / 2f - travelPx)
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val wasOpen = menuOpenState.value
                            down.consume()
                            // Closed: wait to see whether this is a tap (switch screen) or a hold or drag (open the fan).
                            var movedEarly = false
                            if (!wasOpen) {
                                val ended = withTimeoutOrNull(viewConfiguration.longPressTimeoutMillis) {
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        val change = event.changes.firstOrNull { it.id == down.id } ?: return@withTimeoutOrNull true
                                        if (!change.pressed) {
                                            change.consume()
                                            return@withTimeoutOrNull true
                                        }
                                        if ((change.position - down.position).getDistance() > viewConfiguration.touchSlop) {
                                            return@withTimeoutOrNull false
                                        }
                                        change.consume()
                                    }
                                    @Suppress("UNREACHABLE_CODE")
                                    false
                                }
                                if (ended == true) {
                                    onSwitchState.value()
                                    return@awaitEachGesture
                                }
                                movedEarly = ended == false
                                onOpenState.value(true)
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            dragging = true

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
                            var moved = movedEarly
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

// The dial stays dark in both themes, like the board, so it carries its own bright action colours.
private val DialPlate = Color(0xFF1B1D21)
private val DialSeam = Color(0xFF111214)
private val DialHubRing = Color(0xFF3A3D44)
private val DialInner = 54.dp
private val DialOuter = 138.dp
private val DialRim = 8.dp
private val DialLift = 14.dp

private fun dialColor(action: FabAction): Color = when (action) {
    FabAction.Add -> Color(0xFF1FA35C)
    FabAction.Edit -> Color(0xFFE08A00)
    FabAction.Update -> Color(0xFF3B82F6)
    FabAction.Delete -> Color(0xFFFF5A4E)
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
