package ayodong.emobilize.feature.tasks.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ayodong.emobilize.core.model.FilterKey
import ayodong.emobilize.core.model.Task
import ayodong.emobilize.core.model.TaskStatus
import ayodong.emobilize.core.model.deadlineRank
import ayodong.emobilize.core.model.formatInputDate
import ayodong.emobilize.core.model.inputToHours
import ayodong.emobilize.core.model.isOverdue
import ayodong.emobilize.core.model.parseDisplayTime
import ayodong.emobilize.core.ui.dock.FabAction
import ayodong.emobilize.core.ui.theme.AppMetrics
import ayodong.emobilize.core.ui.theme.LocalPalette
import ayodong.emobilize.core.ui.theme.appStyle
import ayodong.emobilize.core.ui.theme.categoryColor
import ayodong.emobilize.core.ui.theme.menuColor
import ayodong.emobilize.core.ui.theme.monoStyle
import ayodong.emobilize.feature.tasks.domain.BoardFilter
import ayodong.emobilize.feature.tasks.domain.departureDate
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val filters = listOf(
    BoardFilter.Today to "Today",
    BoardFilter.Upcoming to "Upcoming",
    BoardFilter.NoDate to "No date",
)

private val dividerDate: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE d MMM", Locale.US)

private val boardDate: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.US)
private val boardClock: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.US)

// Column widths of the departure board, shared by the header row and every task row.
private val TimeColumn = 52.dp
private val LineColumn = 30.dp
private val StatusColumn = 78.dp
private val ColumnGap = 10.dp

@Composable
fun TasksScreen(
    state: TasksUiState,
    pending: FabAction?,
    selectedTaskId: Long?,
    onSelectFilter: (BoardFilter) -> Unit,
    onOpenTask: (Long) -> Unit,
    onEditTask: (Long) -> Unit,
    onSelectTask: (Long) -> Unit,
    onCancelPending: () -> Unit,
    onConfirmDelete: () -> Unit,
    onApplyStatus: (TaskStatus) -> Unit,
    onAddTask: () -> Unit,
    onCompleteTask: (Long) -> Unit,
    onDeleteTask: (Long) -> Unit,
    onUndo: () -> Unit,
    onDismissUndo: () -> Unit,
    modifier: Modifier = Modifier,
    // Header buttons from the shell (Reminders, Settings), drawn at the right of the clock row.
    headerActions: @Composable () -> Unit = {},
    // Lets the shell wrap each card (for the shared transition into the detail sheet) without the
    // list knowing about navigation or animation scopes.
    cardContainer: @Composable (taskId: Long, content: @Composable () -> Unit) -> Unit = { _, content -> content() },
) {
    val palette = LocalPalette.current
    val haptics = LocalHapticFeedback.current
    val visible = state.visibleTasks()
    val doneIds = state.doneIds
    val progressIds = state.progressIds
    val doneCount = visible.count { it.id in doneIds }
    val now by rememberMinuteClock()
    val nowHours = now.hour + now.minute / 60f
    // Board order stays as before: to-do, then in progress, then done, each by deadline.
    val rows = listOf(TaskStatus.Todo, TaskStatus.Progress, TaskStatus.Done).flatMap { status ->
        val group = visible.filter { task ->
            when (status) {
                TaskStatus.Done -> task.id in doneIds
                TaskStatus.Progress -> task.id !in doneIds && task.id in progressIds
                TaskStatus.Todo -> task.id !in doneIds && task.id !in progressIds
            }
        }
        when {
            status == TaskStatus.Done && !state.showCompletedTasks -> emptyList()
            status == TaskStatus.Done -> group
            else -> group.sortedBy { deadlineRank(it) }
        }
    }
    // Upcoming splits the board by departure day; the other tabs are one run of rows.
    val today = LocalDate.now()
    val sections: List<Pair<String?, List<Task>>> = if (state.filter == BoardFilter.Upcoming) {
        rows.groupBy { departureDate(it, today) }
            .toSortedMap(compareBy { it ?: "9999" })
            .map { (date, tasks) -> dayLabel(date, today) to tasks }
    } else {
        listOf(null to rows)
    }
    val next = state.todayTasks(today)
        .filter { it.id !in doneIds }
        .mapNotNull { task -> startHours(task)?.let { task to it } }
        .filter { it.second >= nowHours }
        .minByOrNull { it.second }

    val snackbarHost = remember { SnackbarHostState() }
    val undo = state.undo
    LaunchedEffect(undo) {
        if (undo == null) {
            snackbarHost.currentSnackbarData?.dismiss()
            return@LaunchedEffect
        }
        val result = snackbarHost.showSnackbar(
            message = undo.message,
            actionLabel = "Undo",
            duration = SnackbarDuration.Short,
        )
        if (result == SnackbarResult.ActionPerformed) onUndo() else onDismissUndo()
    }

    Column(modifier.fillMaxSize()) {
        // Compact header: clock over date on the left, the shell's buttons on the right, one 44dp row.
        Row(
            Modifier
                .padding(start = AppMetrics.headerHorizontal, end = AppMetrics.headerHorizontal, top = 12.dp)
                .heightIn(min = 44.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = now.format(boardClock),
                    style = appStyle(28.sp, FontWeight.ExtraBold, letterSpacing = (-1).sp, lineHeight = 30.sp)
                        .copy(fontFeatureSettings = "tnum"),
                    color = palette.text,
                    maxLines = 1,
                )
                Text(
                    text = LocalDate.now().format(boardDate).uppercase(Locale.US),
                    style = appStyle(11.sp, FontWeight.Bold, letterSpacing = 1.sp),
                    color = palette.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            headerActions()
        }

        Row(
            Modifier
                .padding(start = AppMetrics.pageHorizontal, end = AppMetrics.pageHorizontal, top = 8.dp)
                .fillMaxWidth()
                .border(2.dp, palette.text),
        ) {
            filters.forEachIndexed { index, (key, label) ->
                val active = state.filter == key
                if (index > 0) Box(Modifier.width(2.dp).height(44.dp).background(palette.text))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .background(if (active) palette.primary else palette.card)
                        .selectable(selected = active, role = Role.Tab) { onSelectFilter(key) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label.uppercase(),
                        style = appStyle(13.sp, FontWeight.Bold, letterSpacing = 1.sp),
                        color = if (active) palette.onFilled else palette.text,
                        maxLines = 1,
                    )
                }
            }
        }

        pending?.let { action ->
            SelectionBanner(
                action = action,
                onCancel = onCancelPending,
                modifier = Modifier.padding(start = AppMetrics.pageHorizontal, end = AppMetrics.pageHorizontal, top = 10.dp),
            )
        }

        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = AppMetrics.pageHorizontal, end = AppMetrics.pageHorizontal, top = 14.dp, bottom = 14.dp),
        ) {
            val allDone = visible.isNotEmpty() && visible.all { it.id in doneIds }
            if (rows.isEmpty() && allDone && state.filter == BoardFilter.Today) {
                LastService(count = visible.size, onAddTask = onAddTask)
            } else Column(Modifier.fillMaxSize().background(palette.board)) {
                BoardHeader()
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    if (rows.isEmpty()) {
                        EmptyBoard(
                            filter = state.filter,
                            hasAnyTask = state.tasks.isNotEmpty(),
                            allDone = allDone,
                            onAddTask = onAddTask,
                        )
                    } else LazyColumn(Modifier.fillMaxSize()) {
                        sections.forEach { (label, sectionRows) ->
                        if (label != null) {
                            item(key = "day-$label") { Box(Modifier.animateItem()) { DayDivider(label) } }
                        }
                        items(sectionRows, key = { it.id }) { task ->
                            val isDone = task.id in doneIds
                            Box(Modifier.animateItem()) {
                                cardContainer(task.id) {
                                    SwipeableTaskCard(
                                        task = task,
                                        isDone = isDone,
                                        enabled = pending == null,
                                        onComplete = {
                                            haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                                            onCompleteTask(task.id)
                                        },
                                        onDelete = {
                                            haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                                            onDeleteTask(task.id)
                                        },
                                    ) {
                                        BoardRow(
                                            task = task,
                                            status = when {
                                                isDone -> BoardStatus.Departed
                                                task.id in progressIds -> BoardStatus.Boarding
                                                isOverdue(task, nowHours) -> BoardStatus.Delayed
                                                startHours(task) != null -> BoardStatus.OnTime
                                                else -> BoardStatus.AnyTime
                                            },
                                            isSelected = selectedTaskId == task.id,
                                            pending = pending,
                                            onClick = {
                                                if (pending == null) onOpenTask(task.id)
                                                else onSelectTask(task.id)
                                            },
                                        )
                                    }
                                }
                            }
                        }
                        }
                    }
                    SnackbarHost(
                        hostState = snackbarHost,
                        modifier = Modifier.align(Alignment.BottomCenter),
                    ) { data -> UndoSnackbar(data) }
                }
                NextStrip(next = next?.first, nowHours = nowHours, doneCount = doneCount, total = visible.size)
            }
        }

        val selected = selectedTaskId
        if (pending != null && selected != null && pending != FabAction.Update) {
            val delete = pending == FabAction.Delete
            BoardButton(
                label = if (delete) "Delete task" else "Edit task",
                container = if (delete) palette.danger else palette.card,
                content = if (delete) palette.scheme.destructiveForeground else palette.text,
                modifier = Modifier
                    .padding(start = AppMetrics.pageHorizontal, end = AppMetrics.pageHorizontal, bottom = 12.dp)
                    .fillMaxWidth(),
                onClick = {
                    val id = selectedTaskId ?: return@BoardButton
                    if (delete) {
                        haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                        onConfirmDelete()
                    } else {
                        onEditTask(id)
                    }
                },
            )
        }
        if (pending == FabAction.Update && selected != null) {
            Row(
                Modifier.padding(start = AppMetrics.pageHorizontal, end = AppMetrics.pageHorizontal, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                BoardButton(
                    label = "Start",
                    container = palette.card,
                    content = palette.text,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                        onApplyStatus(TaskStatus.Progress)
                    },
                )
                BoardButton(
                    label = "Mark done",
                    container = palette.signal,
                    content = palette.signalInk,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                        onApplyStatus(TaskStatus.Done)
                    },
                )
            }
        }
    }
}

// Current time, refreshed on each minute boundary so the big clock and the DELAYED states stay true.
@Composable
private fun rememberMinuteClock(): State<LocalTime> = produceState(LocalTime.now().withSecond(0).withNano(0)) {
    while (true) {
        val current = LocalTime.now()
        delay(((60 - current.second) * 1000L - current.nano / 1_000_000L).coerceAtLeast(1L))
        value = LocalTime.now().withSecond(0).withNano(0)
    }
}

private enum class BoardStatus(val label: String) {
    Departed("DEPARTED"),
    Boarding("BOARDING"),
    Delayed("DELAYED"),
    OnTime("ON TIME"),
    AnyTime("ANY TIME"),
}

// Day break inside the Upcoming board, set like a timetable heading.
@Composable
private fun DayDivider(label: String) {
    val palette = LocalPalette.current
    Column(Modifier.fillMaxWidth().background(palette.board)) {
        Text(
            text = label,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { heading() }
                .padding(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 6.dp),
            style = monoStyle(12.sp, letterSpacing = 1.5.sp),
            color = palette.signal,
        )
        Box(Modifier.fillMaxWidth().height(1.dp).background(palette.boardRule))
    }
}

private fun dayLabel(iso: String?, today: LocalDate): String {
    val date = iso?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: return "NO DATE"
    val name = date.format(dividerDate).uppercase(Locale.US)
    return if (date == today.plusDays(1)) "TOMORROW \u00B7 $name" else name
}

@Composable
private fun BoardHeader() {
    val palette = LocalPalette.current
    Column {
        Row(
            Modifier.padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(ColumnGap),
        ) {
            val style = appStyle(11.sp, FontWeight.Bold, letterSpacing = 1.5.sp)
            Text("TIME", Modifier.width(TimeColumn), style = style, color = palette.boardMuted)
            Text("DESTINATION", Modifier.weight(1f), style = style, color = palette.boardMuted)
            Text("LN", Modifier.width(LineColumn), style = style, color = palette.boardMuted)
            Text("STATUS", Modifier.width(StatusColumn), style = style, color = palette.boardMuted, textAlign = TextAlign.End)
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(palette.boardRule))
    }
}

@Composable
private fun BoardRow(
    task: Task,
    status: BoardStatus,
    isSelected: Boolean,
    pending: FabAction?,
    onClick: () -> Unit,
) {
    val palette = LocalPalette.current
    val gone = status == BoardStatus.Departed
    val menu = if (isSelected) pending?.let { palette.menuColor(it) } else null
    val statusColor = when (status) {
        BoardStatus.Departed -> palette.boardGone
        BoardStatus.Boarding -> palette.boardBoarding
        BoardStatus.Delayed -> palette.boardDelayed
        BoardStatus.OnTime, BoardStatus.AnyTime -> palette.signal
    }
    val rowBg = when {
        menu != null -> menu.copy(alpha = 0.28f)
        status == BoardStatus.Boarding -> palette.boardBoardingRow
        else -> palette.board
    }
    Column(
        Modifier
            .fillMaxWidth()
            .alpha(if (pending != null && !isSelected) 0.6f else 1f)
            .background(rowBg)
            .then(if (menu != null) Modifier.border(2.dp, menu) else Modifier)
            .clickable(onClick = onClick, role = Role.Button),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ColumnGap),
        ) {
            Text(
                text = boardTime(task) ?: "--:--",
                modifier = Modifier.width(TimeColumn),
                style = monoStyle(15.sp),
                color = if (gone) palette.boardGone else palette.signal,
                maxLines = 1,
                softWrap = false,
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = task.name,
                    style = appStyle(15.sp, FontWeight.SemiBold),
                    color = if (gone) palette.boardGone else palette.boardText,
                    textDecoration = if (gone) TextDecoration.LineThrough else null,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                via(task)?.let { line ->
                    Text(
                        text = line,
                        style = appStyle(12.sp),
                        color = palette.boardMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Box(Modifier.width(LineColumn), contentAlignment = Alignment.CenterStart) {
                LineDot(task)
            }
            Text(
                text = status.label,
                modifier = Modifier.width(StatusColumn),
                style = monoStyle(11.sp, letterSpacing = 0.5.sp),
                color = statusColor,
                textAlign = TextAlign.End,
                maxLines = 1,
            )
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(palette.boardRule))
    }
}

// The category drawn as a round line badge with its initial; tasks without one ride the grey line.
@Composable
private fun LineDot(task: Task) {
    val palette = LocalPalette.current
    val category = task.category
    val color = category?.let { palette.categoryColor(it) } ?: palette.boardGone
    Box(
        Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(color)
            .semantics { contentDescription = "${category?.name ?: "No"} line" },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = category?.name?.firstOrNull()?.uppercase() ?: "X",
            style = appStyle(12.sp, FontWeight.ExtraBold),
            color = Color.White,
        )
    }
}

@Composable
private fun NextStrip(next: Task?, nowHours: Float, doneCount: Int, total: Int) {
    val palette = LocalPalette.current
    val text = if (next != null) {
        val wait = ((startHours(next) ?: nowHours) - nowHours).coerceAtLeast(0f)
        val line = next.category?.name?.firstOrNull()?.uppercase()
        listOfNotNull("Next: ${next.name}", line, formatWait(wait)).joinToString(" \u00B7 ")
    } else {
        "$doneCount of $total departed"
    }
    Row(
        Modifier
            .fillMaxWidth()
            .background(palette.signal)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ClockIcon(palette.signalInk)
        Text(
            text = text,
            style = appStyle(13.sp, FontWeight.Bold),
            color = palette.signalInk,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

// Nothing on the board for this filter: a yellow notice in the board's own type.
@Composable
private fun EmptyBoard(
    filter: BoardFilter,
    hasAnyTask: Boolean,
    allDone: Boolean,
    onAddTask: () -> Unit,
) {
    val palette = LocalPalette.current
    val (title, body) = when {
        !hasAnyTask -> "NO SERVICES YET" to "Add your first task and it shows up here."
        allDone -> "ALL DEPARTED" to "Every task in this view is done."
        filter == BoardFilter.Today -> "NO DEPARTURES TODAY" to "Tasks with today's date land here."
        filter == BoardFilter.Upcoming -> "NOTHING UPCOMING" to "Tasks dated after today land here, grouped by day."
        filter == BoardFilter.NoDate -> "NO UNDATED TASKS" to "Tasks without a date land here."
        else -> "NO DEPARTURES" to "Add a task to get started."
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(title, style = monoStyle(14.sp), color = palette.signal, textAlign = TextAlign.Center)
        Spacer(Modifier.height(6.dp))
        Text(
            body,
            style = appStyle(13.sp, FontWeight.Medium, lineHeight = 18.sp),
            color = palette.boardMuted,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        BoardButton(
            label = if (hasAnyTask) "Add a task" else "Add your first task",
            container = palette.signal,
            content = palette.signalInk,
            onClick = onAddTask,
        )
    }
}

// Every task in view is done: the station closes for the day.
@Composable
private fun LastService(count: Int, onAddTask: () -> Unit) {
    val palette = LocalPalette.current
    Column(
        Modifier
            .fillMaxSize()
            .background(palette.signal)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Row(Modifier.fillMaxWidth().semantics { contentDescription = "Route complete" }, verticalAlignment = Alignment.CenterVertically) {
            repeat(3) {
                Box(Modifier.size(22.dp).clip(CircleShape).background(palette.signalInk).padding(5.dp).clip(CircleShape).background(palette.signal))
                Box(Modifier.weight(1f).height(8.dp).background(palette.signalInk))
            }
            Box(Modifier.size(44.dp).clip(CircleShape).background(palette.signalInk).padding(6.dp).clip(CircleShape).background(Color.White))
        }
        Spacer(Modifier.height(28.dp))
        Text(
            "The last service has left.",
            style = appStyle(40.sp, FontWeight.ExtraBold, letterSpacing = (-1.5).sp, lineHeight = 40.sp),
            color = palette.signalInk,
        )
        Spacer(Modifier.height(14.dp))
        Text(
            if (count == 1) "The one task in this view is done." else "All $count tasks in this view are done.",
            style = appStyle(17.sp, FontWeight.Medium, lineHeight = 24.sp),
            color = palette.signalInk,
        )
        Spacer(Modifier.height(24.dp))
        BoardButton(
            label = "Schedule a new task",
            container = palette.signalInk,
            content = palette.signal,
            modifier = Modifier.fillMaxWidth(),
            onClick = onAddTask,
        )
    }
}

// Square action button with the board's bold uppercase label.
@Composable
private fun BoardButton(
    label: String,
    container: Color,
    content: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val palette = LocalPalette.current
    Box(
        modifier = modifier
            .heightIn(min = 52.dp)
            .background(container)
            .then(if (container == palette.card) Modifier.border(2.dp, palette.text) else Modifier)
            .clickable(onClick = onClick, role = Role.Button)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label.uppercase(),
            style = appStyle(14.sp, FontWeight.ExtraBold, letterSpacing = 1.sp),
            color = content,
            maxLines = 1,
        )
    }
}

@Composable
private fun UndoSnackbar(data: SnackbarData) {
    val palette = LocalPalette.current
    Snackbar(
        snackbarData = data,
        shape = RectangleShape,
        containerColor = palette.signal,
        contentColor = palette.signalInk,
        actionColor = palette.signalInk,
        modifier = Modifier.padding(8.dp),
    )
}

// Swipe right marks the task done, swipe left deletes it. The card springs back and the list
// animates the change, and the snackbar offers Undo. The radial Update and Delete modes stay
// as the non-gesture path. A plain draggable is used instead of SwipeToDismissBox, whose
// anchors produce NaN under the shared-transition lookahead pass and crash.
@Composable
private fun SwipeableTaskCard(
    task: Task,
    isDone: Boolean,
    enabled: Boolean,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    content: @Composable () -> Unit,
) {
    val palette = LocalPalette.current
    val haptics = LocalHapticFeedback.current
    val density = LocalDensity.current
    val threshold = with(density) { 96.dp.toPx() }
    val limit = with(density) { 160.dp.toPx() }
    val offset = remember(task.id) { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var armed by remember { mutableStateOf(false) }
    val dragState = rememberDraggableState { delta ->
        val next = (offset.value + delta).coerceIn(-limit, if (isDone) 0f else limit)
        val nowArmed = abs(next) >= threshold
        if (nowArmed && !armed) haptics.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
        armed = nowArmed
        scope.launch { offset.snapTo(next) }
    }
    Box(Modifier.fillMaxWidth()) {
        val value = offset.value
        if (value != 0f) {
            val toDone = value > 0f
            val color = if (toDone) palette.boardBoarding else palette.boardDelayed
            Row(
                Modifier
                    .matchParentSize()
                    .background(color)
                    .padding(horizontal = 20.dp)
                    .alpha((abs(value) / threshold).coerceIn(0.3f, 1f)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (toDone) Arrangement.Start else Arrangement.End,
            ) {
                Text(
                    if (toDone) "DEPARTED" else "CANCEL",
                    style = monoStyle(12.sp, letterSpacing = 1.sp),
                    color = palette.signalInk,
                )
            }
        }
        Box(
            Modifier
                .offset { IntOffset(value.roundToInt(), 0) }
                .draggable(
                    state = dragState,
                    orientation = Orientation.Horizontal,
                    enabled = enabled,
                    onDragStopped = {
                        val settled = offset.value
                        armed = false
                        when {
                            settled >= threshold -> onComplete()
                            settled <= -threshold -> onDelete()
                        }
                        offset.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                    },
                ),
        ) {
            content()
        }
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
    val message = when (action) {
        FabAction.Edit -> "Select a card to edit"
        FabAction.Delete -> "Select a card to delete"
        FabAction.Update -> "Select a card to update status"
        else -> "Select a card"
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.signal)
            .border(2.dp, palette.signalInk)
            .padding(start = 14.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(12.dp).background(color))
        Spacer(Modifier.width(10.dp))
        Text(
            text = message,
            modifier = Modifier.weight(1f),
            style = appStyle(13.sp, FontWeight.Bold),
            color = palette.signalInk,
        )
        Text(
            text = "CANCEL",
            modifier = Modifier
                .clickable(onClick = onCancel, role = Role.Button)
                .minimumInteractiveComponentSize()
                .padding(horizontal = 8.dp),
            style = appStyle(12.sp, FontWeight.ExtraBold, letterSpacing = 1.sp),
            color = palette.signalInk,
        )
    }
}

@Composable
private fun ClockIcon(color: Color) {
    Canvas(Modifier.size(18.dp)) {
        val stroke = 2.4f * size.minDimension / 24f
        drawCircle(color = color, radius = size.minDimension * 9f / 24f, style = Stroke(width = stroke))
        val center = Offset(size.width / 2f, size.height / 2f)
        drawLine(color, center, Offset(center.x, size.height * 7f / 24f), strokeWidth = stroke, cap = StrokeCap.Square)
        drawLine(color, center, Offset(size.width * 15f / 24f, size.height * 15f / 24f), strokeWidth = stroke, cap = StrokeCap.Square)
    }
}

// Start of the task in hours since midnight, or null when it has no time on the clock.
private fun startHours(task: Task): Float? {
    task.range?.let { return inputToHours(it.startTime) }
    return task.time?.takeIf { it.isNotBlank() }?.let { parseDisplayTime(it) }
}

private fun clock(hours: Float): String {
    val total = (hours * 60f).roundToInt()
    return "%02d:%02d".format(total / 60, total % 60)
}

private fun boardTime(task: Task): String? = startHours(task)?.let(::clock)

private fun formatWait(hours: Float): String {
    val minutes = (hours * 60f).roundToInt()
    return when {
        minutes < 60 -> "$minutes min"
        minutes % 60 == 0 -> "${minutes / 60} h"
        else -> "${minutes / 60} h ${minutes % 60} min"
    }
}

// Second line of a row: priority, then the trip length or the day it runs when not today.
private fun via(task: Task): String? {
    val range = task.range
    val trip = when {
        range != null && range.startDate != range.endDate ->
            "until ${formatInputDate(range.endDate)} ${range.endTime}"
        range != null -> formatWait(inputToHours(range.endTime) - inputToHours(range.startTime))
        task.estimateMinutes != null -> formatWait(task.estimateMinutes / 60f)
        else -> null
    }
    val day = when {
        task.filterKey == FilterKey.Today -> null
        range != null -> formatInputDate(range.startDate)
        else -> task.deadline.takeIf { it.isNotBlank() }
    }
    return listOfNotNull(task.priority?.name, trip, day).joinToString(" \u00B7 ").ifBlank { null }
}
