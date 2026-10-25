package ayodong.emobilize.feature.tasks.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ayodong.emobilize.core.model.FilterKey
import ayodong.emobilize.core.model.ReminderOption
import ayodong.emobilize.core.model.Task
import ayodong.emobilize.core.model.formatInputDate
import ayodong.emobilize.core.model.formatInputTime
import ayodong.emobilize.core.model.inputToHours
import ayodong.emobilize.core.model.isOverdue
import ayodong.emobilize.core.model.parseDisplayTime
import ayodong.emobilize.core.ui.theme.LocalPalette
import ayodong.emobilize.core.ui.theme.appStyle
import ayodong.emobilize.core.ui.theme.categoryColor
import ayodong.emobilize.core.ui.theme.monoStyle
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

private val ticketDate: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM", Locale.US)

// Bar widths for the decorative barcode at the foot of the ticket.
private val barWidths = listOf(3, 1, 2, 1, 4, 1, 1, 3, 2, 1, 1, 2, 4, 1, 2, 1, 3, 1, 1, 2, 1, 4, 2, 1, 3, 1, 2, 1, 1, 3, 2, 1)

@Composable
fun TaskDetailScreen(
    task: Task?,
    taskId: Long,
    isDone: Boolean,
    inProgress: Boolean,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    panelModifier: Modifier = Modifier,
) {
    val palette = LocalPalette.current
    val scrimSource = remember { MutableInteractionSource() }
    val panelSource = remember { MutableInteractionSource() }
    var lookedUp by remember(taskId) { mutableStateOf(false) }
    LaunchedEffect(taskId) { lookedUp = true }
    BackHandler(onBack = onBack)

    Box(
        Modifier
            .fillMaxSize()
            .background(palette.board.copy(alpha = 0.96f))
            .clickable(interactionSource = scrimSource, indication = null, onClick = onBack),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    Modifier
                        .heightIn(min = 44.dp)
                        .clickable(onClick = onBack, role = Role.Button)
                        .padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = palette.boardText)
                    Text("BOARD", style = appStyle(13.sp, FontWeight.Bold, letterSpacing = 1.sp), color = palette.boardText)
                }
                Spacer(Modifier.weight(1f))
                Text(
                    "TKT ${taskId.toString().padStart(4, '0')} · ${LocalDate.now().format(ticketDate).uppercase(Locale.US)}",
                    style = monoStyle(12.sp, FontWeight.Medium),
                    color = palette.boardMuted,
                )
            }
            Spacer(Modifier.height(14.dp))
            Column(
                Modifier
                    .then(panelModifier)
                    .fillMaxWidth()
                    .background(Color.White)
                    .clickable(interactionSource = panelSource, indication = null) {},
            ) {
                when {
                    !lookedUp -> Text(
                        "Loading task",
                        modifier = Modifier.padding(18.dp),
                        style = appStyle(16.sp, FontWeight.Medium),
                        color = palette.signalInk,
                    )
                    task == null -> Column(Modifier.padding(18.dp)) {
                        Text(
                            "This ticket is no longer valid.",
                            style = appStyle(22.sp, FontWeight.ExtraBold, lineHeight = 28.sp),
                            color = palette.signalInk,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "The task may have been deleted.",
                            style = appStyle(14.sp, FontWeight.Medium),
                            color = TicketMuted,
                        )
                    }
                    else -> Ticket(task = task, done = isDone, inProgress = inProgress)
                }
            }
            Spacer(Modifier.weight(1f))
            if (lookedUp) {
                Spacer(Modifier.height(20.dp))
                if (task != null) {
                    TicketButton(
                        label = "Edit this ticket",
                        container = palette.signal,
                        content = palette.signalInk,
                        onClick = onEdit,
                    )
                    Spacer(Modifier.height(10.dp))
                }
                TicketButton(
                    label = "Close",
                    container = Color.Transparent,
                    content = palette.boardText,
                    onClick = onBack,
                )
            }
        }
    }
}

// Ticket paper stays white with black ink in both themes; these are its own tones.
private val TicketInk = Color(0xFF111214)
private val TicketMuted = Color(0xFF4A4D55)
private val TicketRule = Color(0xFFC9CDD3)

@Composable
private fun Ticket(
    task: Task,
    done: Boolean,
    inProgress: Boolean,
) {
    val palette = LocalPalette.current
    val line = task.category?.let { palette.categoryColor(it) } ?: palette.boardGone
    val overdue = !done && isOverdue(task, LocalTime.now().let { it.hour + it.minute / 60f })
    val (stamp, stampColor) = when {
        done -> "ARRIVED" to Color(0xFF0057B8)
        inProgress -> "BOARDING" to Color(0xFF007A4D)
        overdue -> "DELAYED" to Color(0xFFC4161C)
        else -> "ON TIME" to TicketInk
    }
    val from = if (inProgress) "Started" else "To-do"
    val journey = if (task.range != null) "Through journey" else "Single journey"

    Row(
        Modifier
            .fillMaxWidth()
            .background(line)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier.size(36.dp).clip(CircleShape).background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                task.category?.name?.firstOrNull()?.uppercase() ?: "X",
                style = appStyle(17.sp, FontWeight.ExtraBold),
                color = line,
            )
        }
        Column(Modifier.weight(1f)) {
            Text(
                "${(task.category?.name ?: "No").uppercase(Locale.US)} LINE",
                style = appStyle(12.sp, FontWeight.Bold, letterSpacing = 1.5.sp),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                listOfNotNull(journey, task.priority?.let { "${it.name} priority" }).joinToString(" · "),
                style = appStyle(13.sp),
                color = Color.White.copy(alpha = 0.9f),
                maxLines = 1,
            )
        }
    }

    Column(Modifier.padding(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(Modifier.weight(1f)) {
                FactLabel("FROM")
                Text(from, style = appStyle(22.sp, FontWeight.ExtraBold), color = TicketInk)
            }
            Canvas(Modifier.size(width = 40.dp, height = 16.dp)) {
                val stroke = 2.4.dp.toPx()
                val y = size.height / 2f
                drawLine(TicketInk, Offset(2.dp.toPx(), y), Offset(size.width - 4.dp.toPx(), y), stroke)
                drawLine(TicketInk, Offset(size.width - 10.dp.toPx(), 2.dp.toPx()), Offset(size.width - 4.dp.toPx(), y), stroke)
                drawLine(TicketInk, Offset(size.width - 10.dp.toPx(), size.height - 2.dp.toPx()), Offset(size.width - 4.dp.toPx(), y), stroke)
            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                FactLabel("TO")
                Text("Done", style = appStyle(22.sp, FontWeight.ExtraBold), color = TicketInk)
            }
        }
        Spacer(Modifier.height(18.dp))
        Text(
            task.name,
            style = appStyle(30.sp, FontWeight.ExtraBold, letterSpacing = (-1).sp, lineHeight = 32.sp),
            color = if (done) TicketMuted else TicketInk,
            textDecoration = if (done) TextDecoration.LineThrough else null,
        )
        if (task.description.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                task.description,
                style = appStyle(15.sp, lineHeight = 21.sp),
                color = TicketMuted,
            )
        }
        Spacer(Modifier.height(18.dp))
        val facts = listOf(
            "DEPART" to (departure(task) ?: "ANY"),
            "DATE" to (dateLabel(task) ?: "NONE"),
            "TRIP" to (trip(task) ?: "NONE"),
            "PLATFORM" to (task.category?.name ?: "NONE"),
            "CLASS" to (task.priority?.name ?: "NONE"),
            "ALERT" to if (task.reminder == ReminderOption.None) "NONE" else task.reminder.label,
        )
        facts.chunked(3).forEachIndexed { index, row ->
            if (index > 0) Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { (label, value) ->
                    Column(
                        Modifier
                            .weight(1f)
                            .semantics(mergeDescendants = true) {},
                    ) {
                        FactLabel(label)
                        Spacer(Modifier.height(3.dp))
                        Text(
                            value.uppercase(Locale.US),
                            style = monoStyle(15.sp),
                            color = if (label == "DEPART" && overdue) Color(0xFFC4161C) else TicketInk,
                            maxLines = 2,
                        )
                    }
                }
            }
        }
    }

    // Perforation: a dashed tear line between the ticket body and its stub.
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(16.dp)
            .clearAndSetSemantics {},
    ) {
        val y = size.height / 2f
        drawLine(
            TicketRule,
            Offset(20.dp.toPx(), y),
            Offset(size.width - 20.dp.toPx(), y),
            strokeWidth = 2.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 5.dp.toPx())),
        )
        drawCircle(palette.board, radius = 8.dp.toPx(), center = Offset(0f, y))
        drawCircle(palette.board, radius = 8.dp.toPx(), center = Offset(size.width, y))
    }

    Row(
        Modifier.padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            Modifier
                .weight(1f)
                .height(54.dp)
                .clearAndSetSemantics {},
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            barWidths.forEachIndexed { index, width ->
                Box(
                    Modifier
                        .width(width.dp)
                        .fillMaxHeight()
                        .background(if (index % 2 == 0) TicketInk else Color.White),
                )
            }
        }
        Box(
            Modifier
                .rotate(-6f)
                .size(width = 96.dp, height = 54.dp)
                .border(3.dp, stampColor)
                .semantics { contentDescription = "Status: $stamp" },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                stamp,
                style = appStyle(14.sp, FontWeight.ExtraBold, letterSpacing = 1.sp),
                color = stampColor,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun FactLabel(text: String) {
    Text(text, style = appStyle(10.sp, FontWeight.Bold, letterSpacing = 1.5.sp), color = TicketMuted)
}

@Composable
private fun TicketButton(label: String, container: Color, content: Color, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(container)
            .then(if (container == Color.Transparent) Modifier.border(2.dp, content) else Modifier)
            .clickable(onClick = onClick, role = Role.Button),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label.uppercase(Locale.US),
            style = appStyle(15.sp, FontWeight.ExtraBold, letterSpacing = 1.sp),
            color = content,
        )
    }
}

private fun clock(hours: Float): String {
    val total = (hours * 60f).roundToInt()
    return "%02d:%02d".format(total / 60, total % 60)
}

private fun departure(task: Task): String? {
    task.range?.let { return it.startTime }
    return task.time?.takeIf { it.isNotBlank() }?.let { clock(parseDisplayTime(it)) }
}

private fun dateLabel(task: Task): String? {
    val range = task.range
    return when {
        range != null -> formatInputDate(range.startDate)
        task.filterKey == FilterKey.Today && task.deadline.isBlank() -> null
        else -> task.deadline.takeIf { it.isNotBlank() }
    }
}

private fun trip(task: Task): String? {
    val range = task.range
    val minutes = when {
        range != null && range.startDate != range.endDate ->
            return "To ${formatInputDate(range.endDate)}, ${formatInputTime(range.endTime)}"
        range != null -> ((inputToHours(range.endTime) - inputToHours(range.startTime)) * 60f).roundToInt()
        task.estimateMinutes != null -> task.estimateMinutes
        else -> return null
    }
    return when {
        minutes < 60 -> "$minutes min"
        minutes % 60 == 0 -> "${minutes / 60} h"
        else -> "${minutes / 60} h ${minutes % 60}"
    }
}
