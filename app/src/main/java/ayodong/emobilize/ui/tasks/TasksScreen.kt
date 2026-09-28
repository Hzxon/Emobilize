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
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ayodong.emobilize.model.FabAction
import ayodong.emobilize.model.FilterKey
import ayodong.emobilize.model.Priority
import ayodong.emobilize.model.Task
import ayodong.emobilize.model.TaskStatus
import ayodong.emobilize.model.deadlineRank
import ayodong.emobilize.model.formatInputDate
import ayodong.emobilize.model.formatInputTime
import ayodong.emobilize.model.isOverdue
import ayodong.emobilize.ui.theme.AppMetrics
import ayodong.emobilize.ui.theme.LocalPalette
import ayodong.emobilize.ui.theme.appStyle
import ayodong.emobilize.ui.theme.at
import ayodong.emobilize.ui.theme.inkOn
import ayodong.emobilize.ui.theme.categoryColor
import ayodong.emobilize.ui.theme.priorityColor
import java.time.LocalTime

private val filters = listOf(
    FilterKey.All to "ALL",
    FilterKey.Today to "TODAY",
    FilterKey.Tomorrow to "TOMORROW",
    FilterKey.Later to "LATER",
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
            text = "MY TASKS",
            modifier = Modifier.padding(start = AppMetrics.headerHorizontal, end = AppMetrics.headerHorizontal, top = 14.dp),
            style = appStyle(54.sp, FontWeight.Normal, letterSpacing = 1.5.sp, lineHeight = 54.sp),
            color = palette.text,
            maxLines = 1,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = AppMetrics.headerHorizontal, end = AppMetrics.headerHorizontal, top = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            filters.forEach { (key, label) ->
                val active = viewModel.filter == key
                Column(
                    modifier = Modifier
                        .padding(end = 22.dp)
                        .clickable { viewModel.selectFilter(key) }
                        .padding(vertical = 4.dp),
                ) {
                    Text(
                        text = label,
                        style = appStyle(
                            11.5.sp,
                            if (active) FontWeight.Bold else FontWeight.Medium,
                            letterSpacing = 0.07.em,
                        ),
                        color = if (active) palette.text else palette.textMuted,
                    )
                    Spacer(Modifier.height(5.dp))
                    Box(
                        Modifier
                            .width(22.dp)
                            .height(2.5.dp)
                            .clip(CircleShape)
                            .background(if (active) palette.primary else Color.Transparent),
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = "$doneCount / ${visible.size}",
                style = appStyle(12.sp, FontWeight.SemiBold),
                color = palette.primary,
            )
        }

        viewModel.pendingAction?.let { action ->
            SelectionBanner(
                action = action,
                onCancel = viewModel::cancelPending,
                modifier = Modifier.padding(start = AppMetrics.pageHorizontal, end = AppMetrics.pageHorizontal, top = 10.dp),
            )
        }

        LazyColumn(
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
            val color = if (delete) palette.danger else palette.primary
            Box(
                modifier = Modifier
                    .padding(start = AppMetrics.pageHorizontal, end = AppMetrics.pageHorizontal, top = 8.dp, bottom = 12.dp)
                    .fillMaxWidth()
                    .height(AppMetrics.actionHeight)
                    .shadow(8.dp, RoundedCornerShape(AppMetrics.actionRadius), spotColor = color.at(0x40))
                    .clip(RoundedCornerShape(AppMetrics.actionRadius))
                    .background(color)
                    .clickable(onClick = viewModel::confirmAction),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (delete) "Delete Task  →" else "Edit Task  →",
                    style = appStyle(13.sp, FontWeight.Bold, letterSpacing = 0.05.em),
                    color = palette.inkOn(color),
                )
            }
        }
        if (pending == FabAction.Update && selected != null) {
            Column(Modifier.padding(start = AppMetrics.pageHorizontal, end = AppMetrics.pageHorizontal, top = 8.dp, bottom = 12.dp)) {
                Text(
                    text = "SET STATUS",
                    style = appStyle(11.sp, FontWeight.Bold, letterSpacing = 0.08.em),
                    color = palette.actionUpdate,
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
                        label = "✓ Done",
                        container = palette.statusDone,
                        content = palette.inkOn(palette.statusDone),
                        border = null,
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
        Text(label, style = appStyle(13.sp, FontWeight.Bold, letterSpacing = 0.04.em), color = content)
    }
}

@Composable
private fun SelectionBanner(
    action: FabAction,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalPalette.current
    val dot = when (action) {
        FabAction.Delete -> palette.danger
        FabAction.Update -> palette.actionUpdate
        else -> palette.primary
    }
    val labelColor = when (action) {
        FabAction.Delete -> palette.danger
        FabAction.Update -> palette.actionUpdate
        else -> palette.primaryDark
    }
    val background = when (action) {
        FabAction.Delete -> palette.selBgDelete
        FabAction.Update -> palette.selBgUpdate
        else -> palette.primary.at(0x18)
    }
    val border = when (action) {
        FabAction.Delete -> palette.selBorderDelete.at(0x40)
        FabAction.Update -> palette.selBorderUpdate
        else -> palette.primary.at(0x40)
    }
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
            .background(background)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(dot))
        Spacer(Modifier.width(8.dp))
        Text(
            text = message,
            modifier = Modifier.weight(1f),
            style = appStyle(12.sp, FontWeight.Bold),
            color = labelColor,
        )
        Text(
            text = "Cancel",
            modifier = Modifier
                .clickable(onClick = onCancel)
                .padding(horizontal = 6.dp, vertical = 2.dp),
            style = appStyle(11.sp, FontWeight.SemiBold),
            color = if (action == FabAction.Delete) palette.danger else palette.primary,
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
        TaskStatus.Todo -> "TO-DO"
        TaskStatus.Progress -> "IN PROGRESS"
        TaskStatus.Done -> "DONE"
    }
    Row(Modifier.padding(top = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(8.dp))
        Text(label, style = appStyle(10.5.sp, FontWeight.Bold, letterSpacing = 0.1.em), color = color)
        Spacer(Modifier.width(8.dp))
        Box(Modifier.weight(1f).height(1.dp).background(color.at(0x25)))
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
    val background = when {
        !isSelected -> palette.card
        pending == FabAction.Delete -> palette.cardSelBgDelete
        pending == FabAction.Update -> palette.cardSelBgUpdate
        else -> palette.primary.at(0x12)
    }
    val borderColor = when {
        !isSelected -> palette.scheme.border
        pending == FabAction.Delete -> palette.danger
        pending == FabAction.Update -> palette.actionUpdate
        else -> palette.primary
    }
    val accent = palette.categoryColor(task.category)
    val glow = when {
        !isSelected -> Color.Transparent
        pending == FabAction.Delete -> palette.danger.at(0x20)
        pending == FabAction.Update -> palette.actionUpdate.at(0x20)
        else -> palette.primary.at(0x25)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (pending != null && !isSelected) 0.6f else 1f)
            .then(
                if (isSelected) {
                    Modifier
                        .clip(RoundedCornerShape(AppMetrics.cardRadius + 4.dp))
                        .background(glow)
                        .padding(3.dp)
                } else {
                    Modifier
                },
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = AppMetrics.cardMinHeight)
                .shadow(
                    elevation = if (isSelected) 0.dp else if (palette.id == 2) 6.dp else 2.dp,
                    shape = shape,
                    ambientColor = palette.shadow,
                    spotColor = palette.shadow,
                )
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
                Spacer(Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Priority.entries.forEach { level ->
                        val active = task.priority == level
                        val color = palette.priorityColor(level)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (active) color.at(0x20) else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (active) color.at(0x40) else Color.Transparent,
                                    RoundedCornerShape(20.dp),
                                )
                                .padding(
                                    start = if (active) 5.dp else 0.dp,
                                    end = if (active) 7.dp else 0.dp,
                                    top = 2.dp,
                                    bottom = 2.dp,
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (active) color else palette.textMuted),
                            )
                            if (active) {
                                Spacer(Modifier.width(3.dp))
                                Text(
                                    level.name,
                                    style = appStyle(10.sp, FontWeight.Bold, letterSpacing = 0.04.em),
                                    color = color,
                                )
                            }
                        }
                    }
                }
            }
            TaskMeta(task, overdue)
            }
            Box(Modifier.matchParentSize()) {
                Box(Modifier.width(4.dp).fillMaxHeight().background(accent))
            }
            if (!isSelected && palette.cardHighlight.alpha > 0f) {
                Box(
                    Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(palette.cardHighlight),
                )
            }
        }
    }
}

@Composable
private fun TaskMeta(task: Task, overdue: Boolean) {
    val palette = LocalPalette.current
    val range = task.range
    val dateLabel = if (range != null) {
        val start = formatInputDate(range.startDate)
        val end = formatInputDate(range.endDate)
        if (start == end) start else "$start → $end"
    } else {
        task.deadline
    }
    val timeLabel = when {
        range != null -> "${formatInputTime(range.startTime)} → ${formatInputTime(range.endTime)}"
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
            val timeColor = if (overdue) palette.danger else palette.primary
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
