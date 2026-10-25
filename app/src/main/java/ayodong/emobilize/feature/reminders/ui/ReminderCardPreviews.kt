package ayodong.emobilize.feature.reminders.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ayodong.emobilize.core.model.Category
import ayodong.emobilize.core.model.FilterKey
import ayodong.emobilize.core.model.Priority
import ayodong.emobilize.core.model.ReminderOption
import ayodong.emobilize.core.model.Task
import ayodong.emobilize.core.model.isOverdue
import ayodong.emobilize.core.ui.theme.EmobilizeTheme
import ayodong.emobilize.core.ui.theme.LocalPalette
import ayodong.emobilize.core.ui.theme.NeutralDark
import ayodong.emobilize.core.ui.theme.NeutralLight

// ─────────────────────────────────────────────────────────
// Sample Data
// ─────────────────────────────────────────────────────────

private val sampleTaskReminder = Task(
    id = 1L,
    name = "Finish Backend API",
    category = Category.Work,
    deadline = "Today",
    time = "04:00 PM",
    filterKey = FilterKey.Today,
    priority = Priority.High,
    reminder = ReminderOption.TenMinutesBefore,
)

private val sampleTaskDeadline = Task(
    id = 2L,
    name = "Submit Report & Documentation",
    category = Category.Study,
    deadline = "Today",
    time = "02:00 PM",
    filterKey = FilterKey.Today,
    priority = Priority.High,
    reminder = ReminderOption.AtDeadline,
)

// ─────────────────────────────────────────────────────────
// 4 PREVIEWS REQUESTED:
// 1. Light - Reminder (Yellow) - Collapsed
// 2. Light - Reminder (Yellow) - Expanded
// 3. Dark  - Deadline (Red)    - Collapsed
// 4. Dark  - Deadline (Red)    - Expanded
// ─────────────────────────────────────────────────────────

// 1. Light · Reminder (Kuning) · Collapsed
@Preview(
    name = "1. Light · Reminder (Yellow) · Collapsed",
    showBackground = true,
    widthDp = 380,
)
@Composable
private fun PreviewReminderLightCollapsed() {
    EmobilizeTheme(NeutralLight) {
        val palette = LocalPalette.current
        Box(
            Modifier
                .background(palette.surface)
                .padding(16.dp),
        ) {
            ExpandableReminderCard(
                task = sampleTaskReminder,
                onMarkDone = {},
                onReschedule = {},
                initiallyExpanded = false,
                isOverdue = false,
                dueLabel = "Due in 30 mins • 04:00 PM",
            )
        }
    }
}

// 2. Light · Reminder (Kuning) · Expanded (Uncollapsed)
@Preview(
    name = "2. Light · Reminder (Yellow) · Expanded",
    showBackground = true,
    widthDp = 380,
)
@Composable
private fun PreviewReminderLightExpanded() {
    EmobilizeTheme(NeutralLight) {
        val palette = LocalPalette.current
        Box(
            Modifier
                .background(palette.surface)
                .padding(16.dp),
        ) {
            ExpandableReminderCard(
                task = sampleTaskReminder,
                onMarkDone = {},
                onReschedule = {},
                initiallyExpanded = true,
                isOverdue = false,
                dueLabel = "Due in 30 mins • 04:00 PM",
            )
        }
    }
}

// 3. Dark · Deadline (Merah) · Collapsed
@Preview(
    name = "3. Dark · Deadline (Red) · Collapsed",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 380,
)
@Composable
private fun PreviewDeadlineDarkCollapsed() {
    EmobilizeTheme(NeutralDark) {
        val palette = LocalPalette.current
        Box(
            Modifier
                .background(palette.surface)
                .padding(16.dp),
        ) {
            ExpandableReminderCard(
                task = sampleTaskDeadline,
                onMarkDone = {},
                onReschedule = {},
                initiallyExpanded = false,
                isOverdue = true,
                dueLabel = "Deadline passed • 02:00 PM",
            )
        }
    }
}

// 4. Dark · Deadline (Merah) · Expanded (Uncollapsed)
@Preview(
    name = "4. Dark · Deadline (Red) · Expanded",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 380,
)
@Composable
private fun PreviewDeadlineDarkExpanded() {
    EmobilizeTheme(NeutralDark) {
        val palette = LocalPalette.current
        Box(
            Modifier
                .background(palette.surface)
                .padding(16.dp),
        ) {
            ExpandableReminderCard(
                task = sampleTaskDeadline,
                onMarkDone = {},
                onReschedule = {},
                initiallyExpanded = true,
                isOverdue = true,
                dueLabel = "Deadline passed • 02:00 PM",
            )
        }
    }
}
