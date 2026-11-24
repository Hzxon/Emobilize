package ayodong.emobilize.feature.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ayodong.emobilize.core.model.Category
import ayodong.emobilize.core.model.EventType
import ayodong.emobilize.core.model.Priority
import ayodong.emobilize.core.model.ReminderOption
import ayodong.emobilize.core.model.ScheduleMode
import ayodong.emobilize.core.model.Task
import ayodong.emobilize.core.ui.component.FormField
import ayodong.emobilize.core.ui.component.errorInk
import ayodong.emobilize.core.ui.form.DateField
import ayodong.emobilize.core.ui.form.RangeRow
import ayodong.emobilize.core.ui.form.SectionLabel
import ayodong.emobilize.core.ui.form.TaskDraft
import ayodong.emobilize.core.ui.form.TimeField
import ayodong.emobilize.core.ui.theme.LocalPalette
import ayodong.emobilize.core.ui.theme.appStyle
import ayodong.emobilize.core.ui.theme.at
import ayodong.emobilize.core.ui.theme.categoryColor
import ayodong.emobilize.core.ui.theme.priorityColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalendarFormSheet(
    draft: TaskDraft,
    lockType: Boolean,
    canSubmit: Boolean,
    saveColor: Color,
    submitLabel: String,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
) {
    val palette = LocalPalette.current
    val shape = RectangleShape
    val scrimSource = remember { MutableInteractionSource() }
    val sheetSource = remember { MutableInteractionSource() }
    var showErrors by remember { mutableStateOf(false) }
    val types = if (lockType) listOf(draft.eventType) else EventType.entries.filter { it != EventType.Task }
    Box(
        Modifier
            .fillMaxSize()
            .background(palette.modalBg)
            .imePadding()
            .clickable(interactionSource = scrimSource, indication = null, onClick = onDismiss),
    ) {
        BoxWithConstraints(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clickable(interactionSource = sheetSource, indication = null) {},
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxHeight)
                    .background(palette.scheme.popover)
                    .border(1.dp, palette.scheme.border)
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(start = 22.dp, end = 22.dp, top = 20.dp, bottom = 36.dp),
            ) {
                Box(
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(36.dp)
                        .height(4.dp)
                        .background(palette.textMuted.copy(alpha = 0.25f)),
                )
                Spacer(Modifier.height(18.dp))
                FormField(
                    value = draft.name,
                    onValueChange = { draft.name = it },
                    label = "Title",
                    error = if (showErrors) draft.titleError() else null,
                    keyboardType = KeyboardType.Text,
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, palette.text),
                ) {
                    types.forEachIndexed { index, type ->
                        val active = draft.eventType == type
                        if (index > 0) Box(Modifier.width(2.dp).height(44.dp).background(palette.text))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .background(if (active) palette.primary else palette.card)
                                .selectable(
                                    selected = active,
                                    role = Role.Tab,
                                    enabled = !lockType,
                                    onClick = { draft.eventType = type }
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                type.label.uppercase(),
                                style = appStyle(11.sp, FontWeight.ExtraBold, letterSpacing = 1.sp),
                                color = if (active) palette.onFilled else palette.text,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(22.dp))
                when (draft.eventType) {
                    EventType.Event -> EventFields(draft, categories)
                    EventType.Task -> TaskFields(draft, categories)
                    EventType.RegularSchedule -> ScheduleFields(draft)
                }
                if (draft.eventType == EventType.Event || draft.eventType == EventType.RegularSchedule) {
                    Spacer(Modifier.height(18.dp))
                    ReminderPicker(draft)
                }
                if (showErrors) {
                    val scheduleError = when (draft.eventType) {
                        EventType.Event -> if (draft.date.isBlank()) "Choose a date" else null
                        EventType.Task -> when {
                            draft.includeSchedule && draft.scheduleMode == ScheduleMode.Deadline && draft.date.isBlank() -> "Choose a date"
                            draft.includeSchedule && draft.scheduleMode == ScheduleMode.Range && !draft.rangeValid() -> "End time must be after start time"
                            else -> null
                        }
                        EventType.RegularSchedule -> if (!draft.rangeValid()) "End time must be after start time" else null
                    }
                    if (scheduleError != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            scheduleError,
                            style = appStyle(12.sp, FontWeight.Medium),
                            color = errorInk(palette),
                        )
                    }
                }
                Spacer(Modifier.height(22.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlineAction(
                        label = "CANCEL",
                        enabled = true,
                        modifier = Modifier.weight(1f),
                        onClick = onDismiss,
                    )
                    OutlineAction(
                        label = submitLabel.uppercase(),
                        enabled = true,
                        isPrimary = true,
                        modifier = Modifier.weight(1.45f),
                        onClick = {
                            showErrors = true
                            if (canSubmit) onSubmit()
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EventFields(draft: TaskDraft, categories: List<Category>) {
    val palette = LocalPalette.current
    val timeError = draft.eventTimeError()
    SectionLabel("Date & Time")
    Row(
        modifier = Modifier.padding(top = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DateField(value = draft.date, onValue = { draft.date = it }, modifier = Modifier.weight(1.15f))
        TimeField(
            value = draft.startTime,
            onValue = { draft.startTime = it },
            allowClear = true,
            valid = timeError == null,
            placeholder = "Start",
            modifier = Modifier.weight(1f),
        )
        TimeField(
            value = draft.endTime,
            onValue = { draft.endTime = it },
            allowClear = true,
            valid = timeError == null,
            placeholder = "End",
            modifier = Modifier.weight(1f),
        )
    }
    if (timeError != null) {
        Text(
            timeError,
            modifier = Modifier.padding(top = 5.dp),
            style = appStyle(11.sp),
            color = palette.danger,
        )
    }
    Spacer(Modifier.height(18.dp))
    CategoryPicker(draft, categories)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TaskFields(draft: TaskDraft, categories: List<Category>) {
    val palette = LocalPalette.current
    CategoryPicker(draft, categories)
    Spacer(Modifier.height(14.dp))
    SectionLabel("Priority")
    Row(
        modifier = Modifier.padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Priority.entries.forEach { level ->
            val color = palette.priorityColor(level)
            val selected = draft.priority == level
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RectangleShape)
                    .background(if (selected) color else color.at(0x18))
                    .clickable { draft.priority = level }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    level.name.uppercase(),
                    style = appStyle(11.sp, FontWeight.ExtraBold, letterSpacing = 1.sp),
                    color = if (selected) palette.onDark else color,
                )
            }
        }
    }
    Spacer(Modifier.height(14.dp))
    SectionLabel("Schedule")
    Row(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .border(2.dp, palette.text),
    ) {
        ScheduleMode.entries.forEachIndexed { index, mode ->
            val active = draft.scheduleMode == mode
            val label = if (mode == ScheduleMode.Deadline) "Deadline" else "Time range"
            if (index > 0) Box(Modifier.width(2.dp).height(44.dp).background(palette.text))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .background(if (active) palette.primary else palette.card)
                    .selectable(
                        selected = active,
                        role = Role.Tab,
                        onClick = { draft.scheduleMode = mode }
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    label.uppercase(),
                    style = appStyle(12.sp, FontWeight.ExtraBold, letterSpacing = 1.sp),
                    color = if (active) palette.onFilled else palette.text,
                )
            }
        }
    }
    if (draft.scheduleMode == ScheduleMode.Deadline) {
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            DateField(value = draft.date, onValue = { draft.date = it }, modifier = Modifier.weight(3f))
            TimeField(
                value = draft.time,
                onValue = { draft.time = it },
                allowClear = true,
                modifier = Modifier.weight(2f),
            )
        }
        Text(
            "Time is optional",
            modifier = Modifier.padding(top = 5.dp),
            style = appStyle(11.sp),
            color = palette.textMuted,
        )
    } else {
        ScheduleFields(draft)
    }
}

@Composable
private fun ScheduleFields(draft: TaskDraft) {
    val palette = LocalPalette.current
    RangeRow("FROM", draft.startDate, draft.startTime, minDate = null, rangeValid = true) { date, time ->
        draft.startDate = date
        draft.startTime = time
    }
    val valid = draft.rangeValid()
    RangeRow("TO", draft.endDate, draft.endTime, minDate = draft.startDate, rangeValid = valid) { date, time ->
        draft.endDate = date
        draft.endTime = time
    }
    if (!valid) {
        Text(
            "End time must be after start time",
            modifier = Modifier.padding(top = 5.dp),
            style = appStyle(11.sp),
            color = palette.danger,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReminderPicker(draft: TaskDraft) {
    val palette = LocalPalette.current
    SectionLabel("Reminder")
    FlowRow(
        modifier = Modifier.padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ReminderOption.entries.forEach { option ->
            val active = draft.reminder == option
            Box(
                modifier = Modifier
                    .heightIn(min = 40.dp)
                    .background(if (active) palette.primary else palette.card)
                    .border(1.5.dp, if (active) palette.primary else palette.scheme.border)
                    .clickable { draft.reminder = option }
                    .semantics { selected = active }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    option.label.uppercase(),
                    style = appStyle(11.sp, FontWeight.ExtraBold, letterSpacing = 1.sp),
                    color = if (active) palette.onFilled else palette.text,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryPicker(draft: TaskDraft, categories: List<Category>) {
    val palette = LocalPalette.current
    SectionLabel("Category")
    FlowRow(
        modifier = Modifier.padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        categories.forEach { category ->
            val color = palette.categoryColor(category)
            val selected = draft.category == category
            Text(
                text = category.name.uppercase(),
                modifier = Modifier
                    .clip(RectangleShape)
                    .background(if (selected) color else color.at(0x18))
                    .clickable { draft.category = category }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                style = appStyle(11.sp, FontWeight.ExtraBold, letterSpacing = 1.sp),
                color = if (selected) palette.onDark else color,
            )
        }
    }
}

@Composable
private fun OutlineAction(
    label: String,
    enabled: Boolean,
    modifier: Modifier,
    isPrimary: Boolean = false,
    onClick: () -> Unit,
) {
    val palette = LocalPalette.current
    val bg = if (isPrimary) {
        if (enabled) palette.primary else palette.scheme.muted
    } else {
        palette.card
    }
    val border = if (isPrimary) {
        if (enabled) palette.primary else palette.scheme.border
    } else {
        palette.text
    }
    val text = if (isPrimary) {
        if (enabled) palette.onFilled else palette.textMuted
    } else {
        palette.text
    }
    Box(
        modifier = modifier
            .height(52.dp)
            .background(bg)
            .border(2.dp, border)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = appStyle(13.sp, FontWeight.ExtraBold, letterSpacing = 1.sp),
            color = text,
        )
    }
}
