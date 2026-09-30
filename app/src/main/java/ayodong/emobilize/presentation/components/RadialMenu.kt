package ayodong.emobilize.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

data class RadialMenuItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val color: Color,
)

private const val INNER_FRACTION = 0.30f
private const val OUTER_FRACTION = 0.86f
private const val RING_FRACTION = 0.90f
private const val START_ANGLE = -90f
private val GearSize = 56.dp
private val TouchTargetSize = 64.dp

@Composable
fun RadialMenu(
    items: List<RadialMenuItem>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onItemClick: (RadialMenuItem) -> Unit,
    modifier: Modifier = Modifier,
    selectedRoute: String? = null,
    menuSize: Dp = 340.dp,
    idleAlpha: Float = 0.3f,
    gearBottomGap: Dp = 10.dp,
    bottomMargin: Dp = 16.dp,
) {
    val progress by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow),
        label = "radialProgress",
    )
    var hoverIndex by remember { mutableStateOf<Int?>(null) }

    val count = items.size
    val sweep = 360f / count
    val surface = MaterialTheme.colorScheme.surface
    val accent = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val haptics = LocalHapticFeedback.current

    val density = LocalDensity.current
    val radiusPx = with(density) { menuSize.toPx() / 2f }
    val marginPx = with(density) { bottomMargin.toPx() }
    val gapPx = with(density) { gearBottomGap.toPx() }
    val gearHalfPx = with(density) { GearSize.toPx() / 2f }
    val bottomInsetPx = WindowInsets.navigationBars.getBottom(density)
    val bottomInset = with(density) { bottomInsetPx.toDp() }

    val touchExtra = (TouchTargetSize - GearSize) / 2
    val layerGap = (gearBottomGap - touchExtra).coerceAtLeast(0.dp)
    val layerGapPx = with(density) { layerGap.toPx() }

    val travelPx = marginPx + radiusPx - gapPx - gearHalfPx

    val gearAlpha = lerp(idleAlpha, 1f, progress.coerceIn(0f, 1f))
    val gearElevation = (8 * progress.coerceIn(0f, 1f)).dp

    val currentItems by rememberUpdatedState(items)
    val currentExpanded by rememberUpdatedState(expanded)
    val currentProgress by rememberUpdatedState(progress)
    val currentOnExpandedChange by rememberUpdatedState(onExpandedChange)
    val currentOnItemClick by rememberUpdatedState(onItemClick)

    Box(modifier = modifier.fillMaxSize()) {

        if (progress > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f * progress.coerceIn(0f, 1f)))
                    .then(
                        if (expanded) {
                            Modifier.pointerInput(radiusPx, marginPx, bottomInsetPx, items) {
                                detectTapGestures { offset ->
                                    val c = Offset(
                                        size.width / 2f,
                                        size.height - (bottomInsetPx + marginPx + radiusPx),
                                    )
                                    val h = hitTest(offset, c, radiusPx, currentItems.size)
                                    currentOnExpandedChange(false)
                                    if (h != null) currentOnItemClick(currentItems[h])
                                }
                            }
                        } else Modifier
                    ),
            )
        }

        if (progress > 0.01f) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = bottomInset + bottomMargin)
                    .size(menuSize)
                    .graphicsLayer {
                        scaleX = progress
                        scaleY = progress
                        alpha = progress.coerceIn(0f, 1f)
                    },
            ) {
                Canvas(Modifier.fillMaxSize()) {
                    val r = size.minDimension / 2f
                    val innerR = r * INNER_FRACTION
                    val outerR = r * OUTER_FRACTION
                    val ringInner = r * RING_FRACTION
                    val sliceMid = (innerR + outerR) / 2f
                    val ringMid = (ringInner + r) / 2f

                    items.forEachIndexed { i, item ->
                        val start = START_ANGLE + i * sweep
                        val highlighted = item.route == selectedRoute || hoverIndex == i

                        drawArc(
                            color = if (highlighted) item.color else surface,
                            startAngle = start + 1f,
                            sweepAngle = sweep - 2f,
                            useCenter = false,
                            topLeft = Offset(center.x - sliceMid, center.y - sliceMid),
                            size = Size(sliceMid * 2, sliceMid * 2),
                            style = Stroke(width = outerR - innerR),
                        )
                        drawArc(
                            color = accent,
                            startAngle = start + 2f,
                            sweepAngle = sweep - 4f,
                            useCenter = false,
                            topLeft = Offset(center.x - ringMid, center.y - ringMid),
                            size = Size(ringMid * 2, ringMid * 2),
                            style = Stroke(width = r - ringInner),
                        )
                    }
                }

                items.forEachIndexed { i, item ->
                    val angle = Math.toRadians((START_ANGLE + i * sweep + sweep / 2f).toDouble())
                    val dist = radiusPx * (INNER_FRACTION + OUTER_FRACTION) / 2f
                    val dx = (cos(angle) * dist).roundToInt()
                    val dy = (sin(angle) * dist).roundToInt()
                    val highlighted = item.route == selectedRoute || hoverIndex == i
                    val contentColor = when {
                        !highlighted -> onSurface
                        item.color.luminance() > 0.5f -> Color.Black
                        else -> Color.White
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset { IntOffset(dx, dy) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = contentColor,
                            modifier = Modifier.size(24.dp),
                        )
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = contentColor,
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = bottomInset + gearBottomGap)
                .graphicsLayer {
                    alpha = gearAlpha
                    translationY = -travelPx * progress
                }
                .size(GearSize)
                .shadow(gearElevation, CircleShape)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(28.dp)
                    .rotate(progress * 90f),
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = bottomInset + layerGap)
                .size(TouchTargetSize)
                .semantics {
                    contentDescription = if (expanded) "Close menu" else "Open menu"
                    onClick { currentOnExpandedChange(!currentExpanded); true }
                }
                .pointerInput(radiusPx, marginPx, layerGapPx) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val wasExpanded = currentExpanded
                        if (!wasExpanded) currentOnExpandedChange(true)

                        val wheelCenter = Offset(
                            size.width / 2f,
                            size.height - (marginPx + radiusPx - layerGapPx),
                        )

                        fun updateHover(p: Offset) {
                            val h = if (currentProgress < 0.5f) null
                            else hitTest(p, wheelCenter, radiusPx, currentItems.size)
                            if (h != hoverIndex) {
                                hoverIndex = h
                                if (h != null) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }

                        down.consume()
                        updateHover(down.position)
                        var moved = false

                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            updateHover(change.position)
                            if (!moved &&
                                (change.position - down.position).getDistance() > viewConfiguration.touchSlop
                            ) moved = true
                            change.consume()
                            if (!change.pressed) break
                        }

                        val selected = hoverIndex
                        hoverIndex = null
                        when {
                            selected != null -> {
                                currentOnExpandedChange(false)
                                currentOnItemClick(currentItems[selected])
                            }
                            moved -> currentOnExpandedChange(false)
                            wasExpanded -> currentOnExpandedChange(false)
                            else -> Unit
                        }
                    }
                },
        )
    }
}

private fun hitTest(offset: Offset, center: Offset, radius: Float, count: Int): Int? {
    val dx = offset.x - center.x
    val dy = offset.y - center.y
    val dist = sqrt(dx * dx + dy * dy)

    if (dist < radius * INNER_FRACTION || dist > radius * OUTER_FRACTION) return null

    val deg = Math.toDegrees(atan2(dy, dx).toDouble()).toFloat()
    val normalized = ((deg - START_ANGLE) % 360f + 360f) % 360f
    return (normalized / (360f / count)).toInt().coerceIn(0, count - 1)
}