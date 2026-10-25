package ayodong.emobilize.feature.tasks.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ayodong.emobilize.core.model.FilterKey
import ayodong.emobilize.core.model.ReminderOption
import ayodong.emobilize.core.model.Task
import ayodong.emobilize.core.model.TaskStatus
import ayodong.emobilize.core.model.deadlineRank
import ayodong.emobilize.core.model.formatInputDate
import ayodong.emobilize.core.model.formatInputTime
import ayodong.emobilize.core.model.isOverdue
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
import java.time.LocalTime

private val filters = listOf(
    FilterKey.All to "All",
    FilterKey.Today to "Today",
    FilterKey.Tomorrow to "Tomorrow",
    FilterKey.Later to "Later",
)

@Composable
fun TasksScreen(
    state: TasksUiState,
    pending: FabAction?,
    selectedTaskId: Long?,
    onSelectFilter: (FilterKey) -> Unit,
    onOpenTask: (Long) -> Unit,
    onEditTask: (Long) -> Unit,
    onSelectTask: (Long) -> Unit,
    onCancelPending: () -> Unit,
    onConfirmDelete: () -> Unit,
    onApplyStatus: (TaskStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalPalette.current
    val visible = state.visibleTasks()
    val doneIds = state.doneIds
    val progressIds = state.progressIds
    val doneCount = visible.count { it.id in doneIds }
    val now = LocalTime.now()
    val nowHours = now.hour + now.minute / 60f
    val groups = listOf(TaskStatus.Todo, TaskStatus.Progress, TaskStatus.Done).map { status ->
        val group = visible.filter { task ->
            when (status) {
                TaskStatus.Done -> task.id in doneIds
                TaskStatus.Progress -> task.id !in doneIds && task.id in progressIds
                TaskStatus.Todo -> task.id !in doneIds && task.id !in progressIds
            }
        }
        status to if (status == TaskStatus.Done) group else group.sortedBy { deadlineRank(it) }
    }.filter { it.second.isNotEmpty() && (it.first != TaskStatus.Done || state.showCompletedTasks) }

    Column(modifier.fillMaxSize()) {
        Text(
            text = "My tasks",
            modifier = Modifier.padding(start = AppMetrics.headerHorizontal, end = AppMetrics.headerHorizontal, top = 16.dp),
            style = appStyle(30.sp, FontWeight.SemiBold, lineHeight = 36.sp),
            color = palette.text,
            maxLines = 1,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = AppMetrics.headerHorizontal, end = AppMetrics.headerHorizontal, top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(ShadcnRadius.lg))
                    .background(palette.scheme.muted)
                    .padding(3.dp),
            ) {
                filters.forEach { (key, label) ->
                    val active = state.filter == key
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(ShadcnRadius.md))
                            .background(if (active) palette.scheme.background else Color.Transparent)
                            .clickable(role = Role.Tab) { onSelectFilter(key) }
                            .heightIn(min = 40.dp)
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            style = appStyle(12.sp, FontWeight.Medium),
                            color = if (active) palette.text else palette.textMuted,
                            maxLines = 1,
                        )
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = "$doneCount / ${visible.size}",
                style = appStyle(14.sp, FontWeight.Medium),
                color = palette.textMuted,
            )
        }

        pending?.let { action ->
            SelectionBanner(
                action = action,
                onCancel = onCancelPending,
                modifier = Modifier.padding(start = AppMetrics.pageHorizontal, end = AppMetrics.pageHorizontal, top = 10.dp),
            )
        }

        if (groups.isEmpty()) {
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "No tasks in this view.",
                    style = appStyle(14.sp, FontWeight.Medium),
                    color = palette.textMuted,
                )
            }
        } else LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top = 14.dp),
            contentPadding = PaddingValues(start = AppMetrics.pageHorizontal, end = AppMetrics.pageHorizontal, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            groups.forEach { (status, groupTasks) ->
                item(key = "header-$status") { StatusHeader(status, groupTasks.size) }
                items(groupTasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        isDone = task.id in doneIds,
                        isSelected = selectedTaskId == task.id,
                        pending = pending,
                        overdue = task.id !in doneIds && isOverdue(task, nowHours),
                        onClick = {
                            if (pending == null) onOpenTask(task.id)
                            else onSelectTask(task.id)
                        },
                    )
                }
                item(key = "gap-$status") { Spacer(Modifier.height(6.dp)) }
            }
        }

        val selected = selectedTaskId
        if (pending != null && selected != null && pending != FabAction.Update) {
            val delete = pending == FabAction.Delete
            val color = palette.menuColor(pending)
            val shape = RoundedCornerShape(12.dp)
            Box(
                modifier = Modifier
                    .padding(start = AppMetrics.pageHorizontal, end = AppMetrics.pageHorizontal, top = 8.dp, bottom = 12.dp)
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(shape)
                    .background(color.copy(alpha = 0.12f))
                    .border(1.5.dp, color.copy(alpha = 0.40f), shape)
                    .clickable {
                        val id = selectedTaskId ?: return@clickable
                        if (delete) onConfirmDelete() else onEditTask(id)
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (delete) "Delete task" else "Edit task",
                    style = appStyle(14.sp, FontWeight.Medium),
                    color = color,
                )
            }
        }
        if (pending == FabAction.Update && selected != null) {
            Column(Modifier.padding(start = AppMetrics.pageHorizontal, end = AppMetrics.pageHorizontal, top = 8.dp, bottom = 12.dp)) {
                Text(
                    text = "Set status",
                    style = appStyle(14.sp, FontWeight.Medium),
                    color = palette.menuColor(FabAction.Update),
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusChoice(
                        label = "Progress",
                        container = palette.progBtnBg,
                        content = palette.progBtnColor,
                        border = palette.progBtnBorder,
                        modifier = Modifier.weight(1f),
                        onClick = { onApplyStatus(TaskStatus.Progress) },
                    )
                    StatusChoice(
                        label = "Done",
                        container = palette.statusDone.copy(alpha = 0.12f),
                        content = palette.statusDone,
                        border = palette.statusDone.copy(alpha = 0.40f),
                        modifier = Modifier.weight(1f),
                        onClick = { onApplyStatus(TaskStatus.Done) },
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusChoice(
    label: String,
    container: Color,
    content: Color,
    border: Color?,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .height(44.dp)
            .then(
                if (border == null) {
                    Modifier.shadow(6.dp, shape, spotColor = container.at(0x50))
                } else {
                    Modifier
                },
            )
            .clip(shape)
            .background(container)
            .then(if (border != null) Modifier.border(1.5.dp, border, shape) else Modifier)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = appStyle(14.sp, FontWeight.Medium), color = content)
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
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
            .padding(start = 14.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(8.dp))
        Text(
            text = message,
            modifier = Modifier.weight(1f),
            style = appStyle(12.sp, FontWeight.Bold),
            color = color,
        )
        Text(
            text = "Cancel",
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
private fun StatusHeader(status: TaskStatus, count: Int) {
    val palette = LocalPalette.current
    val color = when (status) {
        TaskStatus.Todo -> palette.statusTodo
        TaskStatus.Progress -> palette.statusProgress
        TaskStatus.Done -> palette.statusDone
    }
    val label = when (status) {
        TaskStatus.Todo -> "To-do"
        TaskStatus.Progress -> "In progress"
        TaskStatus.Done -> "Done"
    }
    Row(
        Modifier.padding(top = 6.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(8.dp))
        Text(
            label.uppercase(),
            style = appStyle(11.sp, FontWeight.Bold, letterSpacing = 0.1.em),
            color = palette.roleInk(color),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            count.toString(),
            style = appStyle(11.sp, FontWeight.SemiBold),
            color = palette.textMuted,
        )
        Spacer(Modifier.width(10.dp))
        Box(Modifier.weight(1f).height(1.dp).background(palette.scheme.border))
    }
}

@Composable
private fun TaskCard(
    task: Task,
    isDone: Boolean,
    isSelected: Boolean,
    pending: FabAction?,
    overdue: Boolean,
    onClick: () -> Unit,
) {
    val palette = LocalPalette.current
    val shape = RoundedCornerShape(AppMetrics.cardRadius)
    val menu = pending?.let { palette.menuColor(it) }
    val background = when {
        !isSelected || menu == null -> palette.card
        else -> menu.copy(alpha = 0.12f)
    }
    val borderColor = if (!isSelected || menu == null) palette.scheme.border else menu
    val accent = task.category?.let { palette.categoryColor(it) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (pending != null && !isSelected) 0.6f else 1f)
            .heightIn(min = AppMetrics.cardMinHeight)
            .shadow(if (isSelected) 0.dp else 1.dp, shape, ambientColor = palette.shadow, spotColor = palette.shadow)
            .clip(shape)
            .background(background)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, shape)
            .clickable(onClick = onClick, role = Role.Button),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = task.name,
                    style = appStyle(15.sp, FontWeight.SemiBold, lineHeight = 20.sp),
                    color = if (isDone) palette.textSubtle else palette.text,
                    textDecoration = if (isDone) TextDecoration.LineThrough else null,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (task.priority != null || accent != null) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        task.priority?.let { level -> RoleBadge(level.name, palette.priorityColor(level)) }
                        task.category?.let { category ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(6.dp).clip(CircleShape).background(accent ?: palette.textMuted))
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    text = category.name,
                                    style = appStyle(12.sp, FontWeight.Medium),
                                    color = if (isDone) palette.textSubtle else palette.textMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
                if (task.reminder != ReminderOption.None) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = task.reminder.label,
                        style = appStyle(12.sp, FontWeight.Medium),
                        color = if (isDone) palette.textSubtle else palette.textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (task.range != null || task.deadline.isNotBlank()) {
                Spacer(Modifier.width(12.dp))
                TaskMeta(task, overdue, isDone)
            }
        }
        if (accent != null) {
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
}

@Composable
private fun RoleBadge(label: String, color: Color) {
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
private fun TaskMeta(task: Task, overdue: Boolean, isDone: Boolean) {
    val palette = LocalPalette.current
    val range = task.range
    val dateLabel = if (range != null) {
        val start = formatInputDate(range.startDate)
        val end = formatInputDate(range.endDate)
        if (start == end) start else "$start \u2013 $end"
    } else {
        task.deadline
    }
    val timeLabel = when {
        range != null -> "${formatInputTime(range.startTime)} \u2013 ${formatInputTime(range.endTime)}"
        task.time != null -> task.time
        else -> null
    }
    Column(
        modifier = Modifier.padding(top = 2.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Text(
            text = dateLabel,
            style = appStyle(12.sp, FontWeight.Medium),
            color = if (isDone) palette.textSubtle else palette.textMuted,
            maxLines = 1,
            softWrap = false,
        )
        if (timeLabel != null) {
            val timeColor = when {
                isDone -> palette.textSubtle
                overdue -> palette.roleInk(palette.danger)
                else -> palette.text
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                ClockIcon(timeColor)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = timeLabel,
                    style = appStyle(12.sp, FontWeight.SemiBold),
                    color = timeColor,
                    maxLines = 1,
                    softWrap = false,
                )
            }
        }
    }
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
