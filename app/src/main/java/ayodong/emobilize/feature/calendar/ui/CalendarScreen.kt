package ayodong.emobilize.feature.calendar.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ayodong.emobilize.core.model.CalView
import ayodong.emobilize.core.model.Category
import ayodong.emobilize.core.model.Priority
import ayodong.emobilize.core.model.ReminderOption
import ayodong.emobilize.core.model.Task
import ayodong.emobilize.core.model.TimeBlock
import ayodong.emobilize.core.model.formatBlockTime
import ayodong.emobilize.core.model.formatInputDate
import ayodong.emobilize.core.model.formatInputTime
import ayodong.emobilize.core.model.inputToHours
import ayodong.emobilize.core.model.monthCells
import ayodong.emobilize.core.model.parseDisplayTime
import ayodong.emobilize.core.model.taskOccursOn
import ayodong.emobilize.core.model.weekOf
import ayodong.emobilize.core.ui.dock.FabAction
import ayodong.emobilize.core.ui.theme.AppMetrics
import ayodong.emobilize.core.ui.theme.LocalPalette
import ayodong.emobilize.core.ui.theme.ShadcnRadius
import ayodong.emobilize.core.ui.theme.appStyle
import ayodong.emobilize.core.ui.theme.at
import ayodong.emobilize.core.ui.theme.categoryColor
import ayodong.emobilize.core.ui.theme.menuColor
import ayodong.emobilize.core.ui.theme.priorityColor
import ayodong.emobilize.core.ui.theme.roleInk
import java.time.DayOfWeek
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
    defaultView: CalView = CalView.Week,
    weekStart: DayOfWeek = DayOfWeek.MONDAY,
    pending: FabAction?,
    selectedTaskId: Long?,
    selectedBlockId: Long?,
    onSelectTask: (Task) -> Unit,
    onSelectBlock: (LocalDate, TimeBlock) -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalPalette.current
    val today = LocalDate.now()
    var activeIso by rememberSaveable { mutableStateOf(today.toString()) }
    var monthIso by rememberSaveable { mutableStateOf(today.withDayOfMonth(1).toString()) }
    var viewName by rememberSaveable(defaultView) { mutableStateOf(defaultView.name) }
    val activeDate = runCatching { LocalDate.parse(activeIso) }.getOrDefault(today)
    val visibleMonth = YearMonth.from(runCatching { LocalDate.parse(monthIso) }.getOrDefault(today))
    val view = runCatching { CalView.valueOf(viewName) }.getOrDefault(CalView.Week)
        .let { if (it == CalView.Day) CalView.Week else it }
    val canDelete = pending == FabAction.Delete && (selectedTaskId != null || selectedBlockId != null)
    val selectDate: (LocalDate) -> Unit = { date ->
        activeIso = date.toString()
        monthIso = date.withDayOfMonth(1).toString()
    }
    val shiftMonth: (Long) -> Unit = { delta ->
        val nextMonth = visibleMonth.plusMonths(delta)
        val nextDate = nextMonth.atDay(activeDate.dayOfMonth.coerceAtMost(nextMonth.lengthOfMonth()))
        monthIso = nextMonth.atDay(1).toString()
        activeIso = nextDate.toString()
    }

    Column(modifier.fillMaxSize()) {
        CalendarHeader(
            month = visibleMonth,
            view = view,
            showToday = activeDate != today,
            onPrev = { shiftMonth(-1) },
            onNext = { shiftMonth(1) },
            onToday = { selectDate(today) },
            onView = { viewName = it.name },
        )
        if (pending == FabAction.Edit || pending == FabAction.Delete) {
            SelectionBanner(
                action = pending,
                onCancel = onCancel,
                modifier = Modifier.padding(horizontal = AppMetrics.pageHorizontal, vertical = 4.dp),
            )
        }
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (view) {
                CalView.Month -> MonthBody(
                    month = visibleMonth,
                    weekStart = weekStart,
                    activeDate = activeDate,
                    today = today,
                    tasks = tasks,
                    blocks = blocks,
                    pending = pending,
                    selectedTaskId = selectedTaskId,
                    selectedBlockId = selectedBlockId,
                    onDate = selectDate,
                    onSelectTask = onSelectTask,
                    onSelectBlock = onSelectBlock,
                    onAdd = onAdd,
                )
                CalView.Week, CalView.Day -> WeekBody(
                    activeDate = activeDate,
                    weekStart = weekStart,
                    today = today,
                    tasks = tasks,
                    blocks = blocks,
                    pending = pending,
                    selectedTaskId = selectedTaskId,
                    selectedBlockId = selectedBlockId,
                    onDate = selectDate,
                    onSelectTask = onSelectTask,
                    onSelectBlock = onSelectBlock,
                    onAdd = onAdd,
                )
            }
        }
        if (canDelete) {
            val shape = RoundedCornerShape(12.dp)
            Box(
                modifier = Modifier
                    .padding(start = AppMetrics.pageHorizontal, end = AppMetrics.pageHorizontal, top = 8.dp, bottom = 12.dp)
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
    showToday: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit,
    onView: (CalView) -> Unit,
) {
    val palette = LocalPalette.current
    Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)) {
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
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Row(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(ShadcnRadius.lg))
                    .background(palette.scheme.muted)
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                listOf(CalView.Month, CalView.Week).forEach { item ->
                    val selected = item == view
                    Box(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(ShadcnRadius.lg))
                            .background(if (selected) palette.scheme.background else Color.Transparent)
                            .clickable(role = Role.Tab) { onView(item) }
                            .heightIn(min = 40.dp),
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
            // Jumps the selection back to today. Hidden while today is already selected.
            AnimatedVisibility(
                visible = showToday,
                enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
                exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.Start),
            ) {
                Text(
                    text = "Today",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clip(RoundedCornerShape(ShadcnRadius.lg))
                        .background(palette.scheme.secondary)
                        .border(1.dp, palette.scheme.border, RoundedCornerShape(ShadcnRadius.lg))
                        .clickable(onClick = onToday, role = Role.Button)
                        .heightIn(min = 46.dp)
                        .padding(horizontal = 16.dp)
                        .wrapContentHeight(),
                    style = appStyle(14.sp, FontWeight.SemiBold),
                    color = palette.text,
                )
            }
        }
    }
}

@Composable
private fun CircleNav(onClick: () -> Unit, icon: @Composable () -> Unit) {
    val palette = LocalPalette.current
    Box(
        Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick, role = Role.Button),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(palette.scheme.secondary)
                .border(1.dp, palette.scheme.border, CircleShape),
        )
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
            .padding(start = 14.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (action == FabAction.Delete) "Select a calendar item to delete" else "Select a calendar item to edit",
            modifier = Modifier.weight(1f),
            style = appStyle(12.sp, FontWeight.Bold),
            color = color,
        )
        Text(
            "Cancel",
            modifier = Modifier
                .clip(RoundedCornerShape(ShadcnRadius.md))
                .clickable(onClick = onCancel, role = Role.Button)
                .minimumInteractiveComponentSize()
                .padding(horizontal = 6.dp),
            style = appStyle(12.sp, FontWeight.SemiBold),
            color = color,
        )
    }
}

@Composable
private fun MonthBody(
    month: YearMonth,
    weekStart: DayOfWeek,
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
    onAdd: () -> Unit,
) {
    val palette = LocalPalette.current
    val cells = monthCells(month, weekStart)
    val weekdayLabels = (0..6).map { offset ->
        weekStart.plus(offset.toLong()).getDisplayName(TextStyle.SHORT, Locale.US).first().uppercaseChar().toString()
    }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.padding(horizontal = 16.dp)) {
            weekdayLabels.forEach { label ->
                Text(
                    label,
                    modifier = Modifier.weight(1f),
                    style = appStyle(11.sp, FontWeight.Bold, letterSpacing = 0.06.em),
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
                .background(palette.scheme.border),
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
            onAdd = onAdd,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun WeekBody(
    activeDate: LocalDate,
    weekStart: DayOfWeek,
    today: LocalDate,
    tasks: List<Task>,
    blocks: Map<LocalDate, List<TimeBlock>>,
    pending: FabAction?,
    selectedTaskId: Long?,
    selectedBlockId: Long?,
    onDate: (LocalDate) -> Unit,
    onSelectTask: (Task) -> Unit,
    onSelectBlock: (LocalDate, TimeBlock) -> Unit,
    onAdd: () -> Unit,
) {
    val palette = LocalPalette.current
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.padding(horizontal = AppMetrics.pageHorizontal)) {
            weekOf(activeDate, weekStart).forEach { date ->
                val active = date == activeDate
                val marked = (blocks[date]?.isNotEmpty() == true) || tasks.any { taskOccursOn(it, date, today) }
                Column(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(ShadcnRadius.lg))
                        .clickable(role = Role.Button) { onDate(date) }
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US).uppercase(Locale.US),
                        style = appStyle(11.sp, FontWeight.SemiBold, letterSpacing = 0.04.em),
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
                .padding(start = AppMetrics.pageHorizontal, end = AppMetrics.pageHorizontal, top = 12.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(palette.scheme.border),
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
            onAdd = onAdd,
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
        Modifier
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(ShadcnRadius.lg))
            .clickable(onClick = onClick, role = Role.Button)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
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
    val key: String,
    val title: String,
    val kind: String,
    val category: Category?,
    val priority: Priority?,
    val whenLabel: String?,
    val reminder: String?,
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
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalPalette.current
    val choosing = pending == FabAction.Edit || pending == FabAction.Delete
    val cards = buildList {
        blocks.forEach { block ->
            add(
                AgendaCard(
                    key = "block-${block.id}",
                    title = block.title,
                    kind = "Regular Schedule",
                    category = block.category,
                    priority = null,
                    whenLabel = "${formatBlockTime(block.start)} to ${formatBlockTime(block.end)}",
                    reminder = block.reminder.reminderLabel(),
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
                    key = "task-${task.id}",
                    title = task.name,
                    kind = task.eventType.label,
                    category = task.category,
                    priority = task.priority,
                    whenLabel = taskWhenLabel(task),
                    reminder = task.reminder.reminderLabel(),
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
        Column(
            modifier
                .fillMaxSize()
                .padding(horizontal = AppMetrics.headerHorizontal),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                "Nothing scheduled",
                style = appStyle(16.sp, FontWeight.SemiBold),
                color = palette.text,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Events and regular schedules for this day show up here.",
                style = appStyle(13.sp, FontWeight.Medium, lineHeight = 18.sp),
                color = palette.textMuted,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Add an event",
                modifier = Modifier
                    .clip(RoundedCornerShape(ShadcnRadius.lg))
                    .background(palette.primary)
                    .clickable(onClick = onAdd, role = Role.Button)
                    .heightIn(min = AppMetrics.actionHeight)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                style = appStyle(14.sp, FontWeight.Bold),
                color = palette.onFilled,
            )
        }
        return
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = AppMetrics.pageHorizontal, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(cards, key = { it.key }) { card ->
            Box(Modifier.animateItem()) {
                AgendaRow(card = card, pending = pending, dimmed = choosing && !card.selected)
            }
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
    Box(
        Modifier
            .fillMaxWidth()
            .alpha(if (dimmed) 0.6f else 1f)
            .heightIn(min = AppMetrics.cardMinHeight)
            .shadow(if (card.selected) 0.dp else 1.dp, shape, ambientColor = palette.shadow, spotColor = palette.shadow)
            .clip(shape)
            .background(if (menu != null) menu.copy(alpha = 0.12f) else palette.card)
            .border(if (card.selected) 2.dp else 1.dp, menu ?: palette.scheme.border, shape)
            .then(if (click != null) Modifier.clickable(onClick = click, role = Role.Button) else Modifier),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
        ) {
            Text(
                card.kind.uppercase(),
                style = appStyle(11.sp, FontWeight.Bold, letterSpacing = 0.1.em),
                color = palette.textMuted,
                maxLines = 1,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                card.title,
                style = appStyle(15.sp, FontWeight.SemiBold, lineHeight = 20.sp),
                color = palette.text,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (card.whenLabel != null) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ClockIcon(palette.text)
                    Spacer(Modifier.width(5.dp))
                    Text(
                        card.whenLabel,
                        style = appStyle(12.sp, FontWeight.SemiBold),
                        color = palette.text,
                        maxLines = 2,
                    )
                }
            }
            if (card.category != null || card.priority != null || card.reminder != null) {
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    card.priority?.let { level -> Badge(level.name, palette.priorityColor(level)) }
                    card.category?.let { category ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(6.dp).clip(CircleShape).background(accent))
                            Spacer(Modifier.width(5.dp))
                            Text(
                                category.name,
                                style = appStyle(12.sp, FontWeight.Medium),
                                color = palette.textMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    card.reminder?.let { reminder ->
                        Text(
                            reminder,
                            style = appStyle(12.sp, FontWeight.Medium),
                            color = palette.textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
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

@Composable
private fun Badge(label: String, color: Color) {
    Text(
        text = label,
        modifier = Modifier
            .clip(RoundedCornerShape(ShadcnRadius.md))
            .background(color.at(0x18))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        style = appStyle(11.sp, FontWeight.SemiBold),
        color = LocalPalette.current.roleInk(color),
    )
}

@Composable
private fun ClockIcon(color: Color) {
    Canvas(Modifier.size(11.dp)) {
        val stroke = 2.5f * size.minDimension / 24f
        drawCircle(color = color, style = Stroke(width = stroke, cap = StrokeCap.Round))
        val center = Offset(size.width / 2f, size.height / 2f)
        drawLine(color, center, Offset(center.x, size.height * 0.28f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(color, center, Offset(size.width * 0.68f, size.height * 0.58f), strokeWidth = stroke, cap = StrokeCap.Round)
    }
}

private fun ReminderOption.reminderLabel(): String? =
    takeIf { it != ReminderOption.None }?.label

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
