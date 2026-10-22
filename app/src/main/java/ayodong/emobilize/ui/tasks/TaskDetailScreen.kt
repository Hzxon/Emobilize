package ayodong.emobilize.ui.tasks

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ayodong.emobilize.domain.model.FabAction
import ayodong.emobilize.domain.model.ReminderOption
import ayodong.emobilize.domain.model.Task
import ayodong.emobilize.domain.model.formatInputDate
import ayodong.emobilize.domain.model.formatInputTime
import ayodong.emobilize.ui.theme.AppMetrics
import ayodong.emobilize.ui.theme.LocalPalette
import ayodong.emobilize.ui.theme.ShadcnRadius
import ayodong.emobilize.ui.theme.appStyle
import ayodong.emobilize.ui.theme.inkOn
import ayodong.emobilize.ui.theme.menuColor

@Composable
fun TaskDetailScreen(
    viewModel: TasksViewModel,
    taskId: Long,
    onBack: () -> Unit,
    onEdit: () -> Unit,
) {
    val palette = LocalPalette.current
    val scrimSource = remember { MutableInteractionSource() }
    val panelSource = remember { MutableInteractionSource() }
    var lookedUp by remember(taskId) { mutableStateOf(false) }
    LaunchedEffect(taskId) { lookedUp = true }
    BackHandler(onBack = onBack)
    val task = viewModel.tasks.find { it.id == taskId }
    val shape = RoundedCornerShape(ShadcnRadius.xl)

    Box(
        Modifier
            .fillMaxSize()
            .background(palette.modalBg)
            .clickable(interactionSource = scrimSource, indication = null, onClick = onBack),
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = AppMetrics.pageHorizontal)
                    .fillMaxWidth()
                    .height(maxHeight * 0.8f)
                    .clip(shape)
                    .background(palette.scheme.popover)
                    .border(1.dp, palette.scheme.border, shape)
                    .clickable(interactionSource = panelSource, indication = null) {}
                    .padding(horizontal = 20.dp),
            ) {
                Column(
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(top = 20.dp),
                ) {
                    when {
                        !lookedUp -> Text(
                            "Loading task",
                            style = appStyle(16.sp, FontWeight.Medium),
                            color = palette.text,
                        )
                        task == null -> {
                            Text(
                                "This task is no longer here.",
                                style = appStyle(20.sp, FontWeight.SemiBold, lineHeight = 28.sp),
                                color = palette.text,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "It may have been deleted.",
                                style = appStyle(14.sp, FontWeight.Medium),
                                color = palette.textMuted,
                            )
                        }
                        else -> TaskDetailBody(
                            task = task,
                            done = task.id in viewModel.done,
                            inProgress = task.id in viewModel.inProgress,
                        )
                    }
                }
                if (lookedUp) {
                    Column(Modifier.padding(top = 16.dp, bottom = 20.dp)) {
                        if (task != null) {
                            DetailButton(label = "Edit task", filled = true, onClick = onEdit)
                            Spacer(Modifier.height(8.dp))
                        }
                        DetailButton(label = "Close", filled = false, onClick = onBack)
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskDetailBody(
    task: Task,
    done: Boolean,
    inProgress: Boolean,
) {
    val palette = LocalPalette.current
    val status = when {
        done -> "Done"
        inProgress -> "In progress"
        else -> "To-do"
    }
    val schedule = when {
        task.range != null -> {
            val range = task.range
            val start = formatInputDate(range.startDate)
            val end = formatInputDate(range.endDate)
            val from = formatInputTime(range.startTime)
            val to = formatInputTime(range.endTime)
            if (start == end) "$start, $from to $to" else "$start, $from to $end, $to"
        }
        task.deadline.isNotBlank() -> {
            val start = listOfNotNull(task.deadline, task.time).joinToString(", ")
            if (task.time != null && task.eventEndTime != null) "$start to ${task.eventEndTime}" else start
        }
        else -> null
    }
    Text(
        task.name,
        style = appStyle(24.sp, FontWeight.SemiBold, lineHeight = 32.sp),
        color = if (done) palette.textSubtle else palette.text,
        textDecoration = if (done) TextDecoration.LineThrough else null,
    )
    Spacer(Modifier.height(16.dp))
    Text("Description", style = appStyle(14.sp, FontWeight.Medium), color = palette.textMuted)
    Spacer(Modifier.height(8.dp))
    Text(
        task.description.ifBlank { "None" },
        style = appStyle(16.sp, lineHeight = 24.sp),
        color = palette.text,
    )
    Spacer(Modifier.height(16.dp))
    DetailRow("Status", status)
    DetailRow("Category", task.category?.name ?: "None")
    DetailRow("Priority", task.priority?.name ?: "None")
    DetailRow("Schedule", schedule ?: "None")
    DetailRow("Reminder", if (task.reminder == ReminderOption.None) "None" else task.reminder.label)
    DetailRow("Estimate", task.estimateMinutes?.let { "$it minutes" } ?: "None")
}

@Composable
private fun DetailRow(label: String, value: String) {
    val palette = LocalPalette.current
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            label,
            modifier = Modifier.weight(0.4f),
            style = appStyle(14.sp, FontWeight.Medium),
            color = palette.textMuted,
        )
        Text(
            value,
            modifier = Modifier.weight(0.6f),
            style = appStyle(14.sp, FontWeight.SemiBold),
            color = palette.text,
        )
    }
}

@Composable
private fun DetailButton(label: String, filled: Boolean, onClick: () -> Unit) {
    val palette = LocalPalette.current
    val edit = palette.menuColor(FabAction.Edit)
    val shape = RoundedCornerShape(ShadcnRadius.lg)
    Box(
        Modifier
            .fillMaxWidth()
            .height(AppMetrics.actionHeight)
            .clip(shape)
            .background(if (filled) edit.copy(alpha = 0.12f) else palette.scheme.secondary)
            .border(
                1.dp,
                if (filled) edit.copy(alpha = 0.45f) else palette.scheme.border,
                shape,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = appStyle(14.sp, FontWeight.Bold),
            color = if (filled) palette.inkOn(edit) else palette.scheme.secondaryForeground,
        )
    }
}
