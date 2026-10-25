package ayodong.emobilize.feature.calendar.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
import ayodong.emobilize.core.ui.theme.appStyle
import ayodong.emobilize.core.ui.theme.categoryColor
import ayodong.emobilize.core.ui.theme.menuColor
import ayodong.emobilize.core.ui.theme.monoStyle
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.math.roundToInt

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
            activeDate = activeDate,
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
            Column(Modifier.fillMaxSize()) {
                when (view) {
                    CalView.Month -> MonthGrid(
                        month = visibleMonth,
                        weekStart = weekStart,
                        activeDate = activeDate,
                        today = today,
                        tasks = tasks,
                        blocks = blocks,
                        onDate = selectDate,
                    )
                    CalView.Week, CalView.Day -> WeekLine(
                        activeDate = activeDate,
                        weekStart = weekStart,
                        today = today,
                        tasks = tasks,
                        blocks = blocks,
                        onDate = selectDate,
                    )
                }
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
        if (canDelete) {
            Box(
                modifier = Modifier
                    .padding(start = AppMetrics.pageHorizontal, end = AppMetrics.pageHorizontal, top = 8.dp, bottom = 12.dp)
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .background(palette.danger)
                    .clickable(onClick = onDelete, role = Role.Button),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "DELETE CALENDAR ITEM",
                    style = appStyle(14.sp, FontWeight.ExtraBold, letterSpacing = 1.sp),
                    color = palette.scheme.destructiveForeground,
                )
            }
        }
    }
}

@Composable
private fun CalendarHeader(
    month: YearMonth,
    activeDate: LocalDate,
    view: CalView,
    showToday: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit,
    onView: (CalView) -> Unit,
) {
    val palette = LocalPalette.current
    val week = activeDate.get(WeekFields.ISO.weekOfWeekBasedYear())
    Column(Modifier.padding(start = AppMetrics.headerHorizontal, end = AppMetrics.headerHorizontal, top = 14.dp, bottom = 8.dp)) {
        Row(verticalAlignment = Alignment.Bottom) {
            Column(Modifier.weight(1f)) {
                Text(
                    "WEEK $week \u00B7 ${month.atDay(1).format(monthKicker).uppercase(Locale.US)}",
                    style = appStyle(13.sp, FontWeight.Bold, letterSpacing = 1.5.sp),
                    color = palette.textMuted,
                    maxLines = 1,
                )
                Text(
                    "Route map",
                    modifier = Modifier.padding(top = 4.dp),
                    style = appStyle(40.sp, FontWeight.ExtraBold, letterSpacing = (-1.5).sp, lineHeight = 42.sp),
                    color = palette.text,
                    maxLines = 1,
                )
            }
            // Jumps the selection back to today. Hidden while today is already selected.
            AnimatedVisibility(
                visible = showToday,
                enter = fadeIn() + expandHorizontally(expandFrom = Alignment.End),
                exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.End),
            ) {
                Box(
                    Modifier
                        .padding(start = 8.dp)
                        .height(44.dp)
                        .background(palette.signal)
                        .border(2.dp, palette.signalInk)
                        .clickable(onClick = onToday, role = Role.Button)
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "TODAY",
                        style = appStyle(13.sp, FontWeight.ExtraBold, letterSpacing = 1.sp),
                        color = palette.signalInk,
                    )
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), verticalAlignment = Alignment.CenterVertically) {
            SquareNav(onClick = onPrev) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous month", tint = palette.text)
            }
            Spacer(Modifier.width(8.dp))
            Row(
                Modifier
                    .weight(1f)
                    .border(2.dp, palette.text),
            ) {
                listOf(CalView.Month, CalView.Week).forEachIndexed { index, item ->
                    val selected = item == view
                    if (index > 0) Box(Modifier.width(2.dp).height(44.dp).background(palette.text))
                    Box(
                        Modifier
                            .weight(1f)
                            .height(44.dp)
                            .background(if (selected) palette.primary else palette.card)
                            .selectable(selected = selected, role = Role.Tab) { onView(item) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = item.name.uppercase(Locale.US),
                            style = appStyle(13.sp, FontWeight.Bold, letterSpacing = 1.sp),
                            color = if (selected) palette.onFilled else palette.text,
                        )
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            SquareNav(onClick = onNext) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next month", tint = palette.text)
            }
        }
    }
}

@Composable
private fun SquareNav(onClick: () -> Unit, icon: @Composable () -> Unit) {
    val palette = LocalPalette.current
    Box(
        Modifier
            .size(48.dp)
            .background(palette.card)
            .border(2.dp, palette.text)
            .clickable(onClick = onClick, role = Role.Button),
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
    val palette = LocalPalette.current
    val color = palette.menuColor(action)
    Row(
        modifier
            .fillMaxWidth()
            .background(palette.signal)
            .border(2.dp, palette.signalInk)
            .padding(start = 14.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(12.dp).background(color))
        Spacer(Modifier.width(10.dp))
        Text(
            text = if (action == FabAction.Delete) "Select a calendar item to delete" else "Select a calendar item to edit",
            modifier = Modifier.weight(1f),
            style = appStyle(13.sp, FontWeight.Bold),
            color = palette.signalInk,
        )
        Text(
            "CANCEL",
            modifier = Modifier
                .clickable(onClick = onCancel, role = Role.Button)
                .minimumInteractiveComponentSize()
                .padding(horizontal = 8.dp),
            style = appStyle(12.sp, FontWeight.ExtraBold, letterSpacing = 1.sp),
            color = palette.signalInk,
        )
    }
}

private fun hasService(
    date: LocalDate,
    today: LocalDate,
    tasks: List<Task>,
    blocks: Map<LocalDate, List<TimeBlock>>,
): Boolean = (blocks[date]?.isNotEmpty() == true) || tasks.any { taskOccursOn(it, date, today) }

// The week drawn as seven stations on one thick line. The picked day is the big yellow stop and
// days with nothing on them are greyed out.
@Composable
private fun WeekLine(
    activeDate: LocalDate,
    weekStart: DayOfWeek,
    today: LocalDate,
    tasks: List<Task>,
    blocks: Map<LocalDate, List<TimeBlock>>,
    onDate: (LocalDate) -> Unit,
) {
    val palette = LocalPalette.current
    Box(
        Modifier
            .padding(start = AppMetrics.pageHorizontal, end = AppMetrics.pageHorizontal, top = 10.dp)
            .fillMaxWidth()
            .height(96.dp),
    ) {
        Box(
            Modifier
                .padding(horizontal = 22.dp)
                .padding(top = 22.dp)
                .fillMaxWidth()
                .height(8.dp)
                .background(palette.text),
        )
        Row(Modifier.fillMaxWidth()) {
            weekOf(activeDate, weekStart).forEach { date ->
                val active = date == activeDate
                val isToday = date == today
                val has = hasService(date, today, tasks, blocks)
                val dow = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US).uppercase(Locale.US)
                val size = if (active) 52.dp else 28.dp
                Column(
                    Modifier
                        .weight(1f)
                        .heightIn(min = 88.dp)
                        .selectable(selected = active, role = Role.Button) { onDate(date) }
                        .semantics {
                            contentDescription = "$dow ${date.dayOfMonth}" +
                                (if (isToday) ", today" else "") +
                                (if (has) "" else ", no service")
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        Modifier
                            .padding(top = if (active) 0.dp else 12.dp)
                            .size(size)
                            .clip(CircleShape)
                            .background(palette.text)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    active -> palette.signal
                                    has -> palette.card
                                    else -> palette.scheme.muted
                                },
                            ),
                    )
                    Spacer(Modifier.height(if (active) 6.dp else 8.dp))
                    Text(
                        if (isToday) "TODAY" else dow,
                        style = appStyle(11.sp, FontWeight.Bold, letterSpacing = 1.sp),
                        color = if (has || active) palette.text else palette.textMuted,
                        maxLines = 1,
                    )
                    Text(
                        date.dayOfMonth.toString(),
                        style = appStyle(16.sp, FontWeight.ExtraBold, lineHeight = 18.sp),
                        color = if (has || active) palette.text else palette.textMuted,
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthGrid(
    month: YearMonth,
    weekStart: DayOfWeek,
    activeDate: LocalDate,
    today: LocalDate,
    tasks: List<Task>,
    blocks: Map<LocalDate, List<TimeBlock>>,
    onDate: (LocalDate) -> Unit,
) {
    val palette = LocalPalette.current
    val cells = monthCells(month, weekStart)
    val weekdayLabels = (0..6).map { offset ->
        weekStart.plus(offset.toLong()).getDisplayName(TextStyle.SHORT, Locale.US).first().uppercaseChar().toString()
    }
    Column(Modifier.padding(horizontal = AppMetrics.pageHorizontal)) {
        Row {
            weekdayLabels.forEach { label ->
                Text(
                    label,
                    modifier = Modifier.weight(1f),
                    style = appStyle(11.sp, FontWeight.Bold, letterSpacing = 1.sp),
                    color = palette.textMuted,
                    textAlign = TextAlign.Center,
                )
            }
        }
        cells.chunked(7).forEach { row ->
            Row(Modifier.fillMaxWidth()) {
                row.forEach { date ->
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        if (date != null) {
                            DayCell(
                                date = date,
                                active = date == activeDate,
                                today = date == today,
                                marked = hasService(date, today, tasks, blocks),
                                onClick = { onDate(date) },
                            )
                        }
                    }
                }
                repeat(7 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
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
            .size(44.dp)
            .background(if (active) palette.primary else Color.Transparent)
            .then(if (today && !active) Modifier.border(2.dp, palette.text) else Modifier)
            .selectable(selected = active, onClick = onClick, role = Role.Button),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            date.dayOfMonth.toString(),
            style = appStyle(13.sp, if (active || today) FontWeight.ExtraBold else FontWeight.Medium)
                .copy(fontFeatureSettings = "tnum"),
            color = if (active) palette.onFilled else palette.text,
        )
        Spacer(Modifier.height(3.dp))
        Box(
            Modifier
                .size(width = 10.dp, height = 3.dp)
                .background(
                    when {
                        !marked -> Color.Transparent
                        active -> palette.onFilled
                        else -> palette.text
                    },
                ),
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

// One stop on the day's route: a time, the track segment with its station dot, and the item.
// NowMarker is the "you are here" point between stops on today's route.
private sealed interface RouteStop {
    data class Stop(val card: AgendaCard) : RouteStop
    data class NowMarker(val hours: Float) : RouteStop
}

private val dayTitle: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE d", Locale.US)
private val monthKicker: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.US)

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
    val now = LocalTime.now()
    val nowHours = now.hour + now.minute / 60f
    val stops: List<RouteStop> = buildList {
        val markNow = date == today && cards.isNotEmpty()
        var placed = false
        cards.forEach { card ->
            if (markNow && !placed && card.sort > nowHours) {
                add(RouteStop.NowMarker(nowHours))
                placed = true
            }
            add(RouteStop.Stop(card))
        }
        if (markNow && !placed) add(RouteStop.NowMarker(nowHours))
    }

    Column(modifier.fillMaxSize()) {
        Box(
            Modifier
                .padding(start = AppMetrics.pageHorizontal, end = AppMetrics.pageHorizontal, top = 6.dp)
                .fillMaxWidth()
                .height(2.dp)
                .background(palette.text),
        )
        Row(
            Modifier.padding(start = AppMetrics.pageHorizontal, end = AppMetrics.pageHorizontal, top = 14.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                if (date == today) "Today \u00B7 ${date.format(dayTitle)}" else date.format(dayTitle),
                modifier = Modifier.weight(1f),
                style = appStyle(18.sp, FontWeight.ExtraBold),
                color = palette.text,
                maxLines = 1,
            )
            Text(
                if (cards.size == 1) "1 stop" else "${cards.size} stops",
                style = appStyle(13.sp, FontWeight.SemiBold),
                color = palette.textMuted,
            )
        }
        if (cards.isEmpty()) {
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = AppMetrics.headerHorizontal),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    "Nothing scheduled",
                    style = appStyle(18.sp, FontWeight.ExtraBold),
                    color = palette.text,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "No service on this day.",
                    style = appStyle(14.sp, FontWeight.Medium, lineHeight = 20.sp),
                    color = palette.textMuted,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(16.dp))
                Box(
                    Modifier
                        .heightIn(min = 52.dp)
                        .background(palette.primary)
                        .clickable(onClick = onAdd, role = Role.Button)
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "ADD AN EVENT",
                        style = appStyle(14.sp, FontWeight.ExtraBold, letterSpacing = 1.sp),
                        color = palette.onFilled,
                    )
                }
            }
            return@Column
        }
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(start = AppMetrics.pageHorizontal, end = AppMetrics.pageHorizontal, top = 4.dp, bottom = 12.dp),
        ) {
            itemsIndexed(stops, key = { _, stop -> if (stop is RouteStop.Stop) stop.card.key else "now" }) { index, stop ->
                val previous = (0 until index).reversed().firstNotNullOfOrNull { (stops[it] as? RouteStop.Stop)?.card }
                Box(Modifier.animateItem()) {
                    when (stop) {
                        is RouteStop.Stop -> RouteRow(
                            card = stop.card,
                            first = index == 0,
                            last = index == stops.lastIndex,
                            pending = pending,
                            dimmed = choosing && !stop.card.selected,
                        )
                        is RouteStop.NowMarker -> NowRow(
                            hours = stop.hours,
                            color = previous?.category?.let { palette.categoryColor(it) } ?: palette.text,
                            first = index == 0,
                            last = index == stops.lastIndex,
                        )
                    }
                }
            }
        }
        val lines = cards.mapNotNull { it.category }.distinctBy { it.id }
        if (lines.isNotEmpty()) {
            Row(
                Modifier.padding(start = AppMetrics.pageHorizontal, end = AppMetrics.pageHorizontal, top = 4.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                lines.forEach { category ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(width = 16.dp, height = 6.dp).background(palette.categoryColor(category)))
                        Spacer(Modifier.width(6.dp))
                        Text(category.name, style = appStyle(12.sp, FontWeight.Bold), color = palette.text, maxLines = 1)
                    }
                }
            }
        }
    }
}

// Left part of a route row: the time and the track with its station dot.
@Composable
private fun RouteTrack(
    time: String,
    timeColor: Color,
    lineColor: Color,
    ring: Color,
    dot: Dp,
    first: Boolean,
    last: Boolean,
) {
    val palette = LocalPalette.current
    Text(
        time,
        modifier = Modifier.width(48.dp).padding(top = 10.dp),
        style = monoStyle(13.sp),
        color = timeColor,
        maxLines = 1,
        softWrap = false,
    )
    Spacer(Modifier.width(10.dp))
    Box(Modifier.width(28.dp).fillMaxHeight(), contentAlignment = Alignment.TopCenter) {
        Box(
            Modifier
                .padding(top = if (first) 18.dp else 0.dp)
                .width(8.dp)
                .then(if (last) Modifier.height(if (first) 0.dp else 18.dp) else Modifier.fillMaxHeight())
                .background(lineColor),
        )
        if (dot > 0.dp) {
            Box(
                Modifier
                    .padding(top = 8.dp)
                    .size(dot)
                    .clip(CircleShape)
                    .background(ring)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(palette.card),
            )
        }
    }
    Spacer(Modifier.width(10.dp))
}

@Composable
private fun RouteRow(card: AgendaCard, first: Boolean, last: Boolean, pending: FabAction?, dimmed: Boolean) {
    val palette = LocalPalette.current
    val color = card.category?.let { palette.categoryColor(it) } ?: palette.text
    val menu = if (card.selected) pending?.let { palette.menuColor(it) } else null
    val click = card.onClick
    val via = listOfNotNull(card.kind, card.category?.name, card.priority?.name, card.whenLabel, card.reminder)
        .joinToString(" \u00B7 ")
    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .heightIn(min = 64.dp)
            .alpha(if (dimmed) 0.6f else 1f),
    ) {
        RouteTrack(
            time = if (card.sort < 24f) clock(card.sort) else "ANY",
            timeColor = palette.text,
            lineColor = color,
            ring = color,
            dot = 20.dp,
            first = first,
            last = last,
        )
        Column(
            Modifier
                .weight(1f)
                .padding(bottom = 6.dp)
                .then(if (menu != null) Modifier.background(menu.copy(alpha = 0.14f)).border(2.dp, menu) else Modifier)
                .then(if (click != null) Modifier.clickable(onClick = click, role = Role.Button) else Modifier)
                .heightIn(min = 44.dp)
                .padding(start = if (menu != null) 8.dp else 0.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                card.title,
                style = appStyle(16.sp, FontWeight.Bold),
                color = palette.text,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                via,
                style = appStyle(13.sp),
                color = palette.textMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun NowRow(hours: Float, color: Color, first: Boolean, last: Boolean) {
    val palette = LocalPalette.current
    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .heightIn(min = 48.dp),
    ) {
        RouteTrack(
            time = clock(hours),
            timeColor = palette.text,
            lineColor = if (first) Color.Transparent else color,
            ring = color,
            dot = 0.dp,
            first = false,
            last = last,
        )
        Box(
            Modifier
                .padding(top = 6.dp)
                .background(palette.signal)
                .border(2.dp, palette.signalInk)
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text(
                "YOU ARE HERE",
                style = appStyle(12.sp, FontWeight.ExtraBold, letterSpacing = 1.sp),
                color = palette.signalInk,
            )
        }
    }
}

private fun clock(hours: Float): String {
    val total = (hours * 60f).roundToInt()
    return "%02d:%02d".format(total / 60, total % 60)
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
