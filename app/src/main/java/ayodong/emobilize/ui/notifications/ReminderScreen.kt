package ayodong.emobilize.ui.notifications

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ayodong.emobilize.domain.model.FilterKey
import ayodong.emobilize.domain.model.ReminderOption
import ayodong.emobilize.domain.model.Task
import ayodong.emobilize.domain.model.deadlineRank
import ayodong.emobilize.domain.model.isOverdue
import ayodong.emobilize.ui.theme.AppMetrics
import ayodong.emobilize.ui.theme.LocalPalette
import ayodong.emobilize.ui.theme.appStyle
import java.time.LocalTime

@Composable
fun ReminderScreen(
    tasks: List<Task>,
    doneIds: Set<Long>,
    notificationsEnabled: Boolean,
    onNotificationsChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onMarkDone: (Long) -> Unit,
    onReschedule: (Long) -> Unit,
) {
    val palette = LocalPalette.current
    val nowHours = LocalTime.now().hour + LocalTime.now().minute / 60f
    val reminders = tasks
        .filter { task -> task.id !in doneIds && (task.reminder != ReminderOption.None || !task.time.isNullOrBlank()) }
        .sortedWith(compareBy({ task -> if (isOverdue(task, nowHours)) 0 else 1 }, { task -> deadlineRank(task) }))
    BackHandler(onBack = onBack)
    Column(
        Modifier
            .fillMaxSize()
            .background(palette.surface)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = AppMetrics.pageHorizontal),
    ) {
        Row(
            Modifier.padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = palette.text)
            }
            Text(
                "Reminders",
                modifier = Modifier.padding(start = 8.dp),
                style = appStyle(24.sp, FontWeight.SemiBold, lineHeight = 32.sp),
                color = palette.text,
            )
        }
        PushToggleCard(
            enabled = notificationsEnabled,
            onChange = onNotificationsChange,
            modifier = Modifier.padding(top = 16.dp),
        )
        if (!notificationsEnabled) {
            Text(
                "Notifications are off. Turn them on to preview reminders.",
                modifier = Modifier.padding(top = 28.dp),
                style = appStyle(15.sp, FontWeight.Medium),
                color = palette.textMuted,
            )
        } else if (reminders.isEmpty()) {
            Text(
                "No reminders right now.",
                modifier = Modifier.padding(top = 28.dp),
                style = appStyle(15.sp, FontWeight.Medium),
                color = palette.textMuted,
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(top = 20.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(reminders, key = { it.id }) { task ->
                    val overdue = isOverdue(task, nowHours)
                    val due = listOf(task.deadline, task.time).filter { !it.isNullOrBlank() }.joinToString(", ")
                    val dueLabel = when {
                        overdue -> "Deadline passed • ${task.time ?: task.deadline}"
                        task.reminder != ReminderOption.None -> "${task.reminder.label} • $due"
                        task.filterKey == FilterKey.Today && !task.time.isNullOrBlank() -> "Due in 30 mins • ${task.time}"
                        else -> listOf(task.deadline, task.time).filter { !it.isNullOrBlank() }.joinToString(" • ")
                    }
                    ExpandableReminderCard(
                        task = task,
                        isOverdue = overdue,
                        dueLabel = dueLabel,
                        onMarkDone = { onMarkDone(task.id) },
                        onReschedule = { onReschedule(task.id) },
                    )
                }
            }
        }
    }
}

// Stands in for the system notification permission. It only changes app state; nothing is requested from Android.
@Composable
private fun PushToggleCard(
    enabled: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalPalette.current
    val shape = RoundedCornerShape(AppMetrics.cardRadius)
    Row(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(palette.card)
            .border(1.dp, palette.scheme.border, shape)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text("Push notifications", style = appStyle(14.sp, FontWeight.SemiBold), color = palette.text)
            Spacer(Modifier.height(2.dp))
            Text(
                if (enabled) "Emobilize can alert you before a deadline" else "Reminders are paused",
                style = appStyle(12.sp),
                color = palette.textMuted,
            )
        }
        Switch(checked = enabled, onCheckedChange = onChange)
    }
}
