package ayodong.emobilize.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ayodong.emobilize.domain.model.CalView
import ayodong.emobilize.domain.model.Category
import ayodong.emobilize.domain.model.FabAction
import ayodong.emobilize.domain.model.Task
import ayodong.emobilize.domain.model.TimeBlock
import ayodong.emobilize.domain.model.formatBlockTime
import ayodong.emobilize.domain.model.formatInputDate
import ayodong.emobilize.domain.model.formatInputTime
import ayodong.emobilize.domain.model.inputToHours
import ayodong.emobilize.domain.model.monthCells
import ayodong.emobilize.domain.model.parseDisplayTime
import ayodong.emobilize.domain.model.taskOccursOn
import ayodong.emobilize.domain.model.weekOf
import ayodong.emobilize.ui.theme.AppMetrics
import ayodong.emobilize.ui.theme.LocalPalette
import ayodong.emobilize.ui.theme.ShadcnRadius
import ayodong.emobilize.ui.theme.appStyle
import ayodong.emobilize.ui.theme.at
import ayodong.emobilize.ui.theme.categoryColor
import ayodong.emobilize.ui.theme.menuColor
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val monthTitle = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US)

@Composable
fun CalendarScreen(
    tasks: List<Task>,
    blocks: Map<LocalDate, List<TimeBlock>>,
    pending: FabAction?,
    selectedTaskId: Long?,
    selectedBlockId: Long?,
    onSelectTask: (Task) -> Unit,
    onSelectBlock: (LocalDate, TimeBlock) -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalPalette.current
    val today = LocalDate.now()
    var activeIso by rememberSaveable { mutableStateOf(today.toString()) }
    var monthIso by rememberSaveable { mutableStateOf(today.withDayOfMonth(1).toString()) }
    var viewName by rememberSaveable { mutableStateOf(CalView.Week.name) }
    val activeDate = runCatching { LocalDate.parse(activeIso) }.getOrDefault(today)
    val visibleMonth = YearMonth.from(runCatching { LocalDate.parse(monthIso) }.getOrDefault(today))
    val view = runCatching { CalView.valueOf(viewName) }.getOrDefault(CalView.Week)
    val canDelete = pending == FabAction.Delete && (selectedTaskId != null || selectedBlockId != null)

    Column(modifier.fillMaxSize()) {
        CalendarHeader(
            month = visibleMonth,
            view = view,
            onPrev = { monthIso = visibleMonth.minusMonths(1).atDay(1).toString() },
            onNext = { monthIso = visibleMonth.plusMonths(1).atDay(1).toString() },
            onView = { viewName = it.name },
        )
        if (pending == FabAction.Edit || pending == FabAction.Delete) {
            SelectionBanner(
                action = pending,
                onCancel = onCancel,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp),
            )
        }
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (view) {
                CalView.Month -> MonthBody(
                    month = visibleMonth,
                    activeDate = activeDate,
                    today = today,
                    tasks = tasks,
                    blocks = blocks,
                    pending = pending,
                    selectedTaskId = selectedTaskId,
                    selectedBlockId = selectedBlockId,
                    onDate = { activeIso = it.toString() },
                    onSelectTask = onSelectTask,
                    onSelectBlock = onSelectBlock,
                )
                CalView.Week -> WeekBody(
                    activeDate = activeDate,
                    today = today,
                    tasks = tasks,
                    blocks = blocks,
                    pending = pending,
                    selectedTaskId = selectedTaskId,
                    selectedBlockId = selectedBlockId,
                    onDate = { activeIso = it.toString() },
                    onSelectTask = onSelectTask,
                    onSelectBlock = onSelectBlock,
                )
                CalView.Day -> DayBody(
                    activeDate = activeDate,
                    today = today,
                    tasks = tasks,
                    blocks = blocks,
                    pending = pending,
                    selectedTaskId = selectedTaskId,
                    selectedBlockId = selectedBlockId,
                    onDate = { activeIso = it.toString() },
                    onSelectTask = onSelectTask,
                    onSelectBlock = onSelectBlock,
                )
            }
        }
        if (canDelete) {
            val shape = RoundedCornerShape(12.dp)
            Box(
                modifier = Modifier
                    .padding(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 12.dp)
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(shape)
                    .background(palette.danger.copy(alpha = 0.12f))
                    .border(1.5.dp, palette.danger.copy(alpha = 0.40f), shape)
                    .clickable(onClick = onDelete),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "Delete calendar item",
                    style = appStyle(13.sp, FontWeight.Bold, letterSpacing = 0.04.em),
                    color = palette.danger,
                )
            }
        }
    }
}

@Composable
private fun CalendarHeader(
    month: YearMonth,
    view: CalView,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onView: (CalView) -> Unit,
) {
    val palette = LocalPalette.current
    Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircleNav(onClick = onPrev) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous month", tint = palette.text)
            }
            Text(
                text = month.atDay(1).format(monthTitle),
                modifier = Modifier.weight(1f),
                style = appStyle(24.sp, FontWeight.SemiBold, lineHeight = 32.sp),
                color = palette.text,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            CircleNav(onClick = onNext) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next month", tint = palette.text)
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(ShadcnRadius.lg))
                .background(palette.scheme.muted)
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            CalView.entries.forEach { item ->
                val selected = item == view
                Box(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(ShadcnRadius.lg))
                        .background(if (selected) palette.scheme.background else Color.Transparent)
                        .clickable { onView(item) }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = item.name.lowercase().replaceFirstChar { it.titlecase(Locale.US) },
                        style = appStyle(14.sp, FontWeight.Medium),
                        color = if (selected) palette.text else palette.textMuted,
                    )
                }
            }
        }
    }
}

@Composable
private fun CircleNav(onClick: () -> Unit, icon: @Composable () -> Unit) {
    val palette = LocalPalette.current
    Box(
        Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(palette.scheme.secondary)
            .border(1.dp, palette.scheme.border, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        icon()
    }
}

@Composable
private fun SelectionBanner(
    action: FabAction,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = LocalPalette.current.menuColor(action)
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(ShadcnRadius.xl))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.45f), RoundedCornerShape(ShadcnRadius.xl))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (action == FabAction.Delete) "Select a calendar item to delete" else "Select a calendar item to edit",
            modifier = Modifier.weight(1f),
            style = appStyle(11.5.sp, FontWeight.Bold),
            color = color,
        )
        Text(
            "Cancel",
            modifier = Modifier.clickable(onClick = onCancel).padding(horizontal = 6.dp, vertical = 2.dp),
            style = appStyle(11.sp, FontWeight.SemiBold),
            color = color,
        )
    }
}

@Composable
private fun MonthBody(
    month: YearMonth,
    activeDate: LocalDate,
    today: LocalDate,
    tasks: List<Task>,
    blocks: Map<LocalDate, List<TimeBlock>>,
    pending: FabAction?,
    selectedTaskId: Long?,
    selectedBlockId: Long?,
    onDate: (LocalDate) -> Unit,
    onSelectTask: (Task) -> Unit,
    onSelectBlock: (LocalDate, TimeBlock) -> Unit,
) {
    val palette = LocalPalette.current
    val cells = monthCells(month)
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.padding(horizontal = 16.dp)) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { label ->
                Text(
                    label,
                    modifier = Modifier.weight(1f),
                    style = appStyle(10.sp, FontWeight.Bold, letterSpacing = 0.06.sp),
                    color = palette.textMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
        cells.chunked(7).forEach { row ->
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                row.forEach { date ->
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        if (date != null) {
                            DayCell(
                                date = date,
                                active = date == activeDate,
                                today = date == today,
                                marked = (blocks[date]?.isNotEmpty() == true) || tasks.any { taskOccursOn(it, date, today) },
                                onClick = { onDate(date) },
                            )
                        }
                    }
                }
                repeat(7 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
        Box(
            Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(palette.primary.at(0x18)),
        )
        DayAgenda(
            date = activeDate,
            tasks = tasks,
            blocks = blocks[activeDate].orEmpty(),
            today = today,
            pending = pending,
            selectedTaskId = selectedTaskId,
            selectedBlockId = selectedBlockId,
            onSelectTask = onSelectTask,
            onSelectBlock = { onSelectBlock(activeDate, it) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun WeekBody(
    activeDate: LocalDate,
    today: LocalDate,
    tasks: List<Task>,
    blocks: Map<LocalDate, List<TimeBlock>>,
    pending: FabAction?,
    selectedTaskId: Long?,
    selectedBlockId: Long?,
    onDate: (LocalDate) -> Unit,
    onSelectTask: (Task) -> Unit,
    onSelectBlock: (LocalDate, TimeBlock) -> Unit,
) {
    val palette = LocalPalette.current
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.padding(horizontal = 14.dp)) {
            weekOf(activeDate).forEach { date ->
                val active = date == activeDate
                val marked = (blocks[date]?.isNotEmpty() == true) || tasks.any { taskOccursOn(it, date, today) }
                Column(
                    Modifier
                        .weight(1f)
                        .clickable { onDate(date) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US).uppercase(Locale.US),
                        style = appStyle(10.sp, FontWeight.SemiBold, letterSpacing = 0.04.sp),
                        color = if (active) palette.primary else palette.textMuted,
                    )
                    Spacer(Modifier.height(4.dp))
                    DayNumber(date.dayOfMonth, active = active, today = date == today)
                    Spacer(Modifier.height(4.dp))
                    Box(
                        Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    !marked -> Color.Transparent
                                    active -> palette.onFilled
                                    else -> palette.primary.at(0x80)
                                },
                            ),
                    )
                }
            }
        }
        Box(
            Modifier
                .padding(start = 14.dp, end = 14.dp, top = 10.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(palette.primary.at(0x18)),
        )
        DayAgenda(
            date = activeDate,
            tasks = tasks,
            blocks = blocks[activeDate].orEmpty(),
            today = today,
            pending = pending,
            selectedTaskId = selectedTaskId,
            selectedBlockId = selectedBlockId,
            onSelectTask = onSelectTask,
            onSelectBlock = { onSelectBlock(activeDate, it) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DayBody(
    activeDate: LocalDate,
    today: LocalDate,
    tasks: List<Task>,
    blocks: Map<LocalDate, List<TimeBlock>>,
    pending: FabAction?,
    selectedTaskId: Long?,
    selectedBlockId: Long?,
    onDate: (LocalDate) -> Unit,
    onSelectTask: (Task) -> Unit,
    onSelectBlock: (LocalDate, TimeBlock) -> Unit,
) {
    val palette = LocalPalette.current
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleNav(onClick = { onDate(activeDate.minusDays(1)) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous day", tint = palette.primary)
            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    activeDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.US),
                    style = appStyle(11.sp, FontWeight.Bold, letterSpacing = 0.08.sp),
                    color = palette.textMuted,
                )
                Text(
                    activeDate.dayOfMonth.toString(),
                    style = appStyle(22.sp, FontWeight.Bold),
                    color = palette.text,
                )
            }
            CircleNav(onClick = { onDate(activeDate.plusDays(1)) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next day", tint = palette.primary)
            }
        }
        Box(
            Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(palette.primary.at(0x18)),
        )
        DayAgenda(
            date = activeDate,
            tasks = tasks,
            blocks = blocks[activeDate].orEmpty(),
            today = today,
            pending = pending,
            selectedTaskId = selectedTaskId,
            selectedBlockId = selectedBlockId,
            onSelectTask = onSelectTask,
            onSelectBlock = { onSelectBlock(activeDate, it) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    active: Boolean,
    today: Boolean,
    marked: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalPalette.current
    Column(
        Modifier.clickable(onClick = onClick).padding(vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DayNumber(date.dayOfMonth, active = active, today = today, diameter = 30.dp)
        Spacer(Modifier.height(3.dp))
        Box(
            Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(
                    when {
                        !marked -> Color.Transparent
                        active -> palette.onFilled
                        else -> palette.primary.at(0x70)
                    },
                ),
        )
    }
}

@Composable
private fun DayNumber(
    day: Int,
    active: Boolean,
    today: Boolean,
    diameter: androidx.compose.ui.unit.Dp = 34.dp,
) {
    val palette = LocalPalette.current
    Box(
        Modifier
            .size(diameter)
            .clip(CircleShape)
            .background(if (active) palette.primary else Color.Transparent)
            .border(
                width = if (today && !active) 2.dp else 2.dp,
                color = if (today && !active) palette.primary else Color.Transparent,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            day.toString(),
            style = appStyle(if (diameter < 32.dp) 12.5.sp else 13.sp, if (active) FontWeight.Bold else FontWeight.Medium),
            color = when {
                active -> palette.onFilled
                today -> palette.primary
                else -> palette.text
            },
        )
    }
}

private data class AgendaCard(
    val title: String,
    val kind: String,
    val category: Category?,
    val detail: String?,
    val whenLabel: String?,
    val sort: Float,
    val selected: Boolean,
    val onClick: (() -> Unit)?,
)

@Composable
private fun DayAgenda(
    date: LocalDate,
    tasks: List<Task>,
    blocks: List<TimeBlock>,
    today: LocalDate,
    pending: FabAction?,
    selectedTaskId: Long?,
    selectedBlockId: Long?,
    onSelectTask: (Task) -> Unit,
    onSelectBlock: (TimeBlock) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalPalette.current
    val choosing = pending == FabAction.Edit || pending == FabAction.Delete
    val cards = buildList {
        blocks.forEach { block ->
            add(
                AgendaCard(
                    title = block.title,
                    kind = "Regular Schedule",
                    category = block.category,
                    detail = null,
                    whenLabel = "${formatBlockTime(block.start)} to ${formatBlockTime(block.end)}",
                    sort = block.start,
                    selected = block.id == selectedBlockId,
                    onClick = { onSelectBlock(block) },
                ),
            )
        }
        tasks.filter { taskOccursOn(it, date, today) }.forEach { task ->
            val picked = selectedTaskId == task.id && choosing
            add(
                AgendaCard(
                    title = task.name,
                    kind = task.eventType.label,
                    category = task.category,
                    detail = task.priority?.name,
                    whenLabel = taskWhenLabel(task),
                    sort = taskSort(task),
                    selected = picked,
                    onClick = if (choosing) {
                        { onSelectTask(task) }
                    } else {
                        null
                    },
                ),
            )
        }
    }.sortedBy { it.sort }
    if (cards.isEmpty()) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "Nothing scheduled",
                style = appStyle(13.sp, FontWeight.Medium),
                color = palette.textMuted,
            )
        }
        return
    }
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        cards.forEach { card ->
            AgendaRow(card = card, pending = pending, dimmed = choosing && !card.selected)
        }
    }
}

@Composable
private fun AgendaRow(card: AgendaCard, pending: FabAction?, dimmed: Boolean) {
    val palette = LocalPalette.current
    val shape = RoundedCornerShape(AppMetrics.cardRadius)
    val accent = card.category?.let { palette.categoryColor(it) } ?: palette.scheme.border
    val menu = if (card.selected) pending?.let { palette.menuColor(it) } else null
    val click = card.onClick
    val info = listOfNotNull(card.kind, card.category?.name, card.detail).joinToString(", ")
    Box(
        Modifier
            .fillMaxWidth()
            .alpha(if (dimmed) 0.6f else 1f)
            .heightIn(min = AppMetrics.cardMinHeight)
            .clip(shape)
            .background(if (menu != null) menu.copy(alpha = 0.12f) else palette.card)
            .border(if (card.selected) 2.dp else 1.dp, menu ?: palette.scheme.border, shape)
            .then(if (click != null) Modifier.clickable(onClick = click) else Modifier),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .heightIn(min = AppMetrics.cardMinHeight),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 14.dp),
            ) {
                Text(
                    card.title,
                    style = appStyle(15.sp, FontWeight.SemiBold, lineHeight = 19.5.sp),
                    color = palette.text,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    info,
                    style = appStyle(12.sp, FontWeight.Medium),
                    color = palette.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (card.whenLabel != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        card.whenLabel,
                        style = appStyle(12.sp, FontWeight.Medium),
                        color = palette.text,
                        maxLines = 2,
                    )
                }
            }
        }
        Box(Modifier.matchParentSize()) {
            Box(
                Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accent),
            )
        }
    }
}

private fun taskWhenLabel(task: Task): String? {
    val range = task.range
    if (range != null) {
        val start = formatInputDate(range.startDate)
        val end = formatInputDate(range.endDate)
        val startTime = formatInputTime(range.startTime)
        val endTime = formatInputTime(range.endTime)
        return if (start == end) "$startTime to $endTime" else "$start, $startTime to $end, $endTime"
    }
    val time = task.time
    val end = task.eventEndTime
    return when {
        time != null && !end.isNullOrBlank() && task.deadline.isNotBlank() -> "${task.deadline}, $time to $end"
        time != null && !end.isNullOrBlank() -> "$time to $end"
        time != null && task.deadline.isNotBlank() -> "${task.deadline}, $time"
        time != null -> time
        task.deadline.isNotBlank() -> task.deadline
        else -> null
    }
}

private fun taskSort(task: Task): Float {
    val range = task.range
    if (range != null) return inputToHours(range.startTime)
    val time = task.time
    if (!time.isNullOrBlank()) return parseDisplayTime(time)
    return 48f
}
