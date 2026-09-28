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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import ayodong.emobilize.model.CalView
import ayodong.emobilize.model.FabAction
import ayodong.emobilize.model.Task
import ayodong.emobilize.model.TimeBlock
import ayodong.emobilize.model.formatBlockTime
import ayodong.emobilize.model.formatHourLabel
import ayodong.emobilize.model.monthCells
import ayodong.emobilize.model.parseDisplayTime
import ayodong.emobilize.model.taskOccursOn
import ayodong.emobilize.model.weekOf
import ayodong.emobilize.ui.theme.LocalPalette
import ayodong.emobilize.ui.theme.ShadcnRadius
import ayodong.emobilize.ui.theme.appStyle
import ayodong.emobilize.ui.theme.at
import ayodong.emobilize.ui.theme.categoryColor
import ayodong.emobilize.ui.theme.inkOn
import ayodong.emobilize.ui.theme.menuColor
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

private val monthTitle = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US)
private val HourHeight = 60.dp
private val DayStart = 0
private val DayEnd = 24

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
    val nowHours = LocalTime.now().let { it.hour + it.minute / 60f }
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
                    nowHours = nowHours,
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
                    nowHours = nowHours,
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
                    nowHours = nowHours,
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
                    "Delete Calendar Item  →",
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
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous month", tint = palette.primary)
            }
            Text(
                text = month.atDay(1).format(monthTitle),
                modifier = Modifier.weight(1f),
                style = appStyle(20.sp, FontWeight.Bold, letterSpacing = 0.5.sp),
                color = palette.text,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            CircleNav(onClick = onNext) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next month", tint = palette.primary)
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(palette.primary.at(0x12))
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            CalView.entries.forEach { item ->
                val selected = item == view
                Box(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(ShadcnRadius.lg))
                        .background(if (selected) palette.card else Color.Transparent)
                        .clickable { onView(item) }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = item.name.lowercase().replaceFirstChar { it.titlecase(Locale.US) },
                        style = appStyle(11.5.sp, FontWeight.Bold, letterSpacing = 0.04.sp),
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
            .background(palette.primary.at(0x0E))
            .border(1.5.dp, palette.primary.at(0x40), CircleShape)
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
    nowHours: Float,
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
        DayTimeline(
            date = activeDate,
            startHour = DayStart,
            endHour = DayEnd,
            tasks = tasks,
            blocks = blocks[activeDate].orEmpty(),
            today = today,
            nowHours = nowHours,
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
    nowHours: Float,
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
        DayTimeline(
            date = activeDate,
            startHour = DayStart,
            endHour = DayEnd,
            tasks = tasks,
            blocks = blocks[activeDate].orEmpty(),
            today = today,
            nowHours = nowHours,
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
    nowHours: Float,
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
        DayTimeline(
            date = activeDate,
            startHour = DayStart,
            endHour = DayEnd,
            tasks = tasks,
            blocks = blocks[activeDate].orEmpty(),
            today = today,
            nowHours = nowHours,
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

@Composable
private fun DayTimeline(
    date: LocalDate,
    startHour: Int,
    endHour: Int,
    tasks: List<Task>,
    blocks: List<TimeBlock>,
    today: LocalDate,
    nowHours: Float,
    pending: FabAction?,
    selectedTaskId: Long?,
    selectedBlockId: Long?,
    onSelectTask: (Task) -> Unit,
    onSelectBlock: (TimeBlock) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalPalette.current
    val hours = endHour - startHour
    val scroll = rememberScrollState()
    val density = LocalDensity.current
    val timedTasks = tasks.filter { task ->
        !task.time.isNullOrBlank() && taskOccursOn(task, date, today)
    }
    LaunchedEffect(date, startHour) {
        val target = ((nowHours - startHour - 2.5f) * with(density) { HourHeight.toPx() }).coerceAtLeast(0f)
        scroll.scrollTo(target.roundToInt())
    }
    Box(
        modifier
            .fillMaxWidth()
            .verticalScroll(scroll)
            .padding(bottom = 56.dp),
    ) {
        Box(
            Modifier
                .padding(start = 54.dp, end = 14.dp)
                .fillMaxWidth()
                .height(HourHeight * hours),
        ) {
            repeat(hours) { index ->
                val hour = startHour + index
                Row(
                    Modifier
                        .offset(x = (-54).dp, y = HourHeight * index)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        formatHourLabel(hour),
                        modifier = Modifier.width(46.dp).offset(y = (-7).dp),
                        style = appStyle(10.5.sp, FontWeight.Medium),
                        color = palette.textMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.End,
                    )
                    Box(
                        Modifier
                            .padding(start = 8.dp)
                            .weight(1f)
                            .height(1.dp)
                            .background(palette.scheme.border),
                    )
                }
                if (index < hours - 1) {
                    Box(
                        Modifier
                            .offset(y = HourHeight * index + HourHeight / 2)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(palette.scheme.border.copy(alpha = 0.45f)),
                    )
                }
            }
            if (date == today && nowHours in startHour.toFloat()..endHour.toFloat()) {
                Row(
                    Modifier
                        .zIndex(2f)
                        .offset(x = (-8).dp, y = HourHeight * (nowHours - startHour))
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(9.dp).clip(CircleShape).background(palette.danger))
                    Box(Modifier.weight(1f).height(1.5.dp).background(palette.danger.copy(alpha = 0.75f)))
                }
            }
            blocks.forEach { block ->
                val top = (block.start - startHour) * 60f
                val height = ((block.end - block.start) * 60f - 4f).coerceAtLeast(22f)
                BlockCard(
                    block = block,
                    selected = block.id == selectedBlockId,
                    pending = pending,
                    compact = height < 36f,
                    modifier = Modifier
                        .offset(x = 2.dp, y = top.dp + 2.dp)
                        .fillMaxWidth()
                        .height(height.dp)
                        .clickable { onSelectBlock(block) },
                )
            }
            timedTasks.forEach { task ->
                val top = (parseDisplayTime(task.time!!) - startHour) * 60f - 12f
                val picked = selectedTaskId == task.id && (pending == FabAction.Edit || pending == FabAction.Delete)
                val menu = pending?.let { palette.menuColor(it) }
                val color = palette.categoryColor(task.category)
                val chip = RoundedCornerShape(6.dp)
                Row(
                    Modifier
                        .zIndex(3f)
                        .offset(y = top.dp)
                        .fillMaxWidth()
                        .height(22.dp)
                        .clip(chip)
                        .background(if (picked && menu != null) menu.copy(alpha = 0.12f) else color.at(0x18))
                        .border(if (picked && menu != null) 1.5.dp else 0.dp, menu ?: Color.Transparent, chip)
                        .clickable(enabled = pending == FabAction.Edit || pending == FabAction.Delete) {
                            onSelectTask(task)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.width(3.dp).fillMaxHeight().background(color))
                    Text(
                        task.name,
                        modifier = Modifier.padding(start = 7.dp, end = 8.dp),
                        style = appStyle(10.sp, FontWeight.Bold),
                        color = color,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (blocks.isEmpty() && timedTasks.isEmpty()) {
                Text(
                    "Nothing scheduled",
                    modifier = Modifier.align(Alignment.Center),
                    style = appStyle(13.sp, FontWeight.Medium),
                    color = palette.primary.copy(alpha = 0.5f),
                )
            }
        }
    }
}

@Composable
private fun BlockCard(
    block: TimeBlock,
    selected: Boolean,
    pending: FabAction?,
    compact: Boolean,
    modifier: Modifier,
) {
    val palette = LocalPalette.current
    val color = palette.categoryColor(block.category)
    val menu = if (selected) pending?.let { palette.menuColor(it) } else null
    val shape = RoundedCornerShape(ShadcnRadius.lg)
    val ink = if (selected && menu == null) palette.inkOn(color) else color
    Row(
        modifier
            .clip(shape)
            .background(if (menu != null) menu.copy(alpha = 0.12f) else if (selected) color else color.copy(alpha = 0.16f))
            .border(if (menu != null) 2.dp else 0.dp, menu ?: Color.Transparent, shape),
    ) {
        Box(Modifier.width(3.dp).fillMaxHeight().background(color))
        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = if (compact) 0.dp else 6.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                block.title,
                style = appStyle(12.sp, FontWeight.Bold),
                color = ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!compact) {
                Text(
                    "${formatBlockTime(block.start)} – ${formatBlockTime(block.end)}",
                    style = appStyle(10.5.sp, FontWeight.Medium),
                    color = ink.copy(alpha = if (selected) 0.8f else 0.7f),
                    maxLines = 1,
                )
            }
        }
    }
}
