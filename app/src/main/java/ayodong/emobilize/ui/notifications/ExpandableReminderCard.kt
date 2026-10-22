package ayodong.emobilize.ui.notifications

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ayodong.emobilize.domain.model.Task
import ayodong.emobilize.ui.theme.AppMetrics
import ayodong.emobilize.ui.theme.EmobilizeLogo
import ayodong.emobilize.ui.theme.LocalPalette
import ayodong.emobilize.ui.theme.appStyle

@Composable
fun ExpandableReminderCard(
    task: Task,
    onMarkDone: () -> Unit,
    onReschedule: () -> Unit,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false,
    isOverdue: Boolean = false,
    dueLabel: String? = null,
) {
    val palette = LocalPalette.current
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "chevronRotation",
    )

    val statusColor = if (isOverdue) palette.prioHigh else palette.prioMedium
    val displayText = dueLabel ?: if (isOverdue) {
        "Deadline passed • ${task.time ?: task.deadline}"
    } else {
        "Due in 30 mins • ${task.time ?: "Scheduled time"}"
    }

    Card(
        shape = RoundedCornerShape(AppMetrics.cardRadius),
        colors = CardDefaults.cardColors(containerColor = palette.card),
        border = BorderStroke(1.dp, palette.scheme.border),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expanded = !expanded },
    ) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 1. Logo Emobilize
                EmobilizeLogo(Modifier.size(44.dp))

                Spacer(Modifier.width(12.dp))

                // 2. Info Teks
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Emobilize",
                        style = appStyle(11.sp, FontWeight.Medium),
                        color = palette.textMuted.copy(alpha = 0.75f),
                        lineHeight = 14.sp,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = task.name,
                        style = appStyle(15.sp, FontWeight.SemiBold),
                        color = palette.text,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp,
                    )
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Clock indicator icon
                        Canvas(modifier = Modifier.size(13.dp)) {
                            val stroke = 1.3.dp.toPx()
                            val radius = size.minDimension / 2f - stroke / 2f
                            val center = Offset(size.width / 2f, size.height / 2f)
                            drawCircle(
                                color = statusColor,
                                radius = radius,
                                center = center,
                                style = Stroke(width = stroke),
                            )
                            // Clock hands (hour & minute)
                            drawLine(
                                color = statusColor,
                                start = center,
                                end = Offset(center.x, center.y - radius * 0.55f),
                                strokeWidth = stroke,
                                cap = StrokeCap.Round,
                            )
                            drawLine(
                                color = statusColor,
                                start = center,
                                end = Offset(center.x + radius * 0.45f, center.y),
                                strokeWidth = stroke,
                                cap = StrokeCap.Round,
                            )
                        }
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = displayText,
                            style = appStyle(12.sp, FontWeight.Medium),
                            color = statusColor,
                            lineHeight = 14.sp,
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

                // 3. Ikon Chevron
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = palette.textMuted,
                    modifier = Modifier
                        .size(22.dp)
                        .rotate(rotationAngle),
                )
            }

            // 4. Dropdown Action Footer
            if (expanded) {
                HorizontalDivider(
                    color = palette.scheme.border,
                    thickness = 1.dp,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable {
                                expanded = false
                                onMarkDone()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Mark as done",
                            style = appStyle(13.sp, FontWeight.SemiBold),
                            color = palette.text,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(20.dp)
                            .background(palette.scheme.border),
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable {
                                expanded = false
                                onReschedule()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Reschedule",
                            style = appStyle(13.sp, FontWeight.Medium),
                            color = palette.textMuted,
                        )
                    }
                }
            }
        }
    }
}
