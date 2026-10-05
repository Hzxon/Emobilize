package ayodong.emobilize.ui.tasks

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ayodong.emobilize.domain.model.FabAction
import ayodong.emobilize.domain.model.FilterKey
import ayodong.emobilize.domain.model.Task
import ayodong.emobilize.domain.model.TaskStatus
import ayodong.emobilize.domain.model.deadlineRank
import ayodong.emobilize.domain.model.formatInputDate
import ayodong.emobilize.domain.model.formatInputTime
import ayodong.emobilize.domain.model.isOverdue
import ayodong.emobilize.ui.theme.AppMetrics
import ayodong.emobilize.ui.theme.LocalPalette
import ayodong.emobilize.ui.theme.ShadcnRadius
import ayodong.emobilize.ui.theme.appStyle
import ayodong.emobilize.ui.theme.at
import ayodong.emobilize.ui.theme.menuColor
import ayodong.emobilize.ui.theme.categoryColor
import ayodong.emobilize.ui.theme.priorityColor
import java.time.LocalTime

private val filters = listOf(
    FilterKey.All to "All",
    FilterKey.Today to "Today",
    FilterKey.Tomorrow to "Tomorrow",
    FilterKey.Later to "Later",
)

@Composable
fun TasksScreen(
    viewModel: TasksViewModel,
    modifier: Modifier = Modifier,
) {
    val palette = LocalPalette.current
    val visible = viewModel.visibleTasks()
    val doneIds = viewModel.done
    val progressIds = viewModel.inProgress
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
    }.filter { it.second.isNotEmpty() }

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
                    val active = viewModel.filter == key
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(ShadcnRadius.md))
                            .background(if (active) palette.scheme.background else Color.Transparent)
                            .clickable { viewModel.selectFilter(key) }
                            .padding(vertical = 6.dp),
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

        viewModel.pendingAction?.let { action ->
            SelectionBanner(
                action = action,
                onCancel = viewModel::cancelPending,
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
                item(key = "header-$status") { StatusHeader(status) }
                items(groupTasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        isDone = task.id in doneIds,
                        isSelected = viewModel.selectedTaskId == task.id,
                        pending = viewModel.pendingAction,
                        overdue = task.id !in doneIds && isOverdue(task, nowHours),
                        onClick = { viewModel.selectTask(task.id) },
                    )
                }
                item(key = "gap-$status") { Spacer(Modifier.height(2.dp)) }
            }
        }

        val pending = viewModel.pendingAction
        val selected = viewModel.selectedTaskId
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
                    .clickable(onClick = viewModel::confirmAction),
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
                        onClick = { viewModel.applyStatus(TaskStatus.Progress) },
                    )
                    StatusChoice(
                        label = "Done",
                        container = palette.statusDone.copy(alpha = 0.12f),
                        content = palette.statusDone,
                        border = palette.statusDone.copy(alpha = 0.40f),
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.applyStatus(TaskStatus.Done) },
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
            .padding(horizontal = 14.dp, vertical = 10.dp),
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
                .clickable(onClick = onCancel)
                .padding(horizontal = 6.dp, vertical = 2.dp),
            style = appStyle(11.sp, FontWeight.SemiBold),
            color = color,
        )
    }
}

@Composable
private fun StatusHeader(status: TaskStatus) {
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
    Row(Modifier.padding(top = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(8.dp))
        Text(label, style = appStyle(14.sp, FontWeight.Medium), color = color)
        Spacer(Modifier.width(8.dp))
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
            .clip(shape)
            .background(background)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, shape)
            .then(if (pending != null) Modifier.clickable(onClick = onClick) else Modifier),
    ) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
            Spacer(Modifier.width(4.dp))
            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp, vertical = 14.dp),
            ) {
                Text(
                    text = task.name,
                    style = appStyle(15.sp, FontWeight.SemiBold, lineHeight = 19.5.sp),
                    color = if (isDone) palette.textSubtle else palette.text,
                    textDecoration = if (isDone) TextDecoration.LineThrough else null,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                task.priority?.let { level ->
                    Spacer(Modifier.height(6.dp))
                    RoleBadge(level.name, palette.priorityColor(level))
                }
            }
            if (task.range != null || task.deadline.isNotBlank()) {
                TaskMeta(task, overdue)
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
        style = appStyle(12.sp, FontWeight.Medium),
        color = color,
    )
}

@Composable
private fun TaskMeta(task: Task, overdue: Boolean) {
    val palette = LocalPalette.current
    val range = task.range
    val dateLabel = if (range != null) {
        val start = formatInputDate(range.startDate)
        val end = formatInputDate(range.endDate)
        if (start == end) start else "$start -> $end"
    } else {
        task.deadline
    }
    val timeLabel = when {
        range != null -> "${formatInputTime(range.startTime)} -> ${formatInputTime(range.endTime)}"
        task.time != null -> task.time
        else -> null
    }
    Column(
        modifier = Modifier.padding(end = 14.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = dateLabel,
            modifier = Modifier.padding(bottom = if (timeLabel == null) 17.dp else 0.dp),
            style = appStyle(if (range != null) 10.sp else 12.sp, FontWeight.Medium),
            color = palette.textMuted,
            maxLines = 1,
            softWrap = false,
        )
        if (timeLabel != null) {
            val timeColor = if (overdue) palette.prioHigh else palette.primary
            Row(verticalAlignment = Alignment.CenterVertically) {
                ClockIcon(timeColor)
                Spacer(Modifier.width(3.dp))
                Text(
                    text = timeLabel,
                    style = appStyle(if (range != null) 10.sp else 11.sp, FontWeight.SemiBold),
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
