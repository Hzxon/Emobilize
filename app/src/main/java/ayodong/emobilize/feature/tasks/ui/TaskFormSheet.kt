package ayodong.emobilize.feature.tasks.ui

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ayodong.emobilize.core.model.Category
import ayodong.emobilize.core.model.Priority
import ayodong.emobilize.core.model.ScheduleMode
import ayodong.emobilize.core.ui.component.FormField
import ayodong.emobilize.core.ui.component.errorInk
import ayodong.emobilize.core.ui.form.DateField
import ayodong.emobilize.core.ui.form.RangeRow
import ayodong.emobilize.core.ui.form.SectionLabel
import ayodong.emobilize.core.ui.form.TaskDraft
import ayodong.emobilize.core.ui.form.TimeField
import ayodong.emobilize.core.ui.theme.LocalPalette
import ayodong.emobilize.core.ui.theme.ShadcnRadius
import ayodong.emobilize.core.ui.theme.appStyle
import ayodong.emobilize.core.ui.theme.at
import ayodong.emobilize.core.ui.theme.categoryColor
import ayodong.emobilize.core.ui.theme.inkOn
import ayodong.emobilize.core.ui.theme.priorityColor

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskFormSheet(
    title: String,
    submitLabel: String,
    draft: TaskDraft,
    canSubmit: Boolean,
    saveColor: Color,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
) {
    val palette = LocalPalette.current
    val shape = RoundedCornerShape(topStart = ShadcnRadius.xl, topEnd = ShadcnRadius.xl)
    val scrimSource = remember { MutableInteractionSource() }
    val sheetSource = remember { MutableInteractionSource() }
    var showErrors by remember { mutableStateOf(false) }
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
                    .clip(shape)
                    .background(palette.scheme.popover)
                    .border(1.dp, palette.scheme.border, shape)
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(start = 22.dp, end = 22.dp, top = 24.dp, bottom = 36.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, style = appStyle(17.sp, FontWeight.Bold, letterSpacing = 0.02.em), color = palette.text)
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(palette.scheme.secondary)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("✕", style = appStyle(16.sp), color = palette.scheme.secondaryForeground)
                    }
                }
                Spacer(Modifier.height(20.dp))
                FormField(
                    value = draft.name,
                    onValueChange = { draft.name = it },
                    label = "Task name",
                    error = if (showErrors) draft.titleError() else null,
                    keyboardType = KeyboardType.Text,
                )
                FormField(
                    value = draft.description,
                    onValueChange = { draft.description = it },
                    label = "Description",
                    error = if (showErrors) draft.descriptionError() else null,
                    keyboardType = KeyboardType.Text,
                    singleLine = false,
                    minLines = 3,
                )
                FormField(
                    value = draft.minutes,
                    onValueChange = { draft.minutes = it },
                    label = "Estimate (minutes)",
                    error = draft.minutesError(),
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                )
                Spacer(Modifier.height(24.dp))
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
                            text = category.name,
                            modifier = Modifier
                                .clip(RoundedCornerShape(ShadcnRadius.md))
                                .background(if (selected) color else color.at(0x18))
                                .clickable { draft.category = category }
                                .padding(horizontal = 12.dp, vertical = 5.dp),
                            style = appStyle(11.5.sp, FontWeight.Bold),
                            color = if (selected) palette.inkOn(color) else color,
                        )
                    }
                }
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
                                .clip(RoundedCornerShape(ShadcnRadius.md))
                                .background(if (selected) color else color.at(0x18))
                                .clickable { draft.priority = level }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                level.name,
                                style = appStyle(12.sp, FontWeight.Bold),
                                color = if (selected) palette.inkOn(color) else color,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
                SectionLabel("Schedule")
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ScheduleMode.entries.forEach { mode ->
                        val active = draft.includeSchedule && draft.scheduleMode == mode
                        val label = if (mode == ScheduleMode.Deadline) "Deadline" else "Time range"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(ShadcnRadius.md))
                                .background(if (active) palette.scheme.primary else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (active) palette.scheme.primary else palette.scheme.border,
                                    RoundedCornerShape(ShadcnRadius.md),
                                )
                                .clickable {
                                    draft.includeSchedule = true
                                    draft.scheduleMode = mode
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                label,
                                style = appStyle(11.5.sp, FontWeight.Bold),
                                color = if (active) palette.scheme.primaryForeground else palette.scheme.mutedForeground,
                            )
                        }
                    }
                }
                if (draft.includeSchedule && draft.scheduleMode == ScheduleMode.Deadline) {
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        DateField(
                            value = draft.date,
                            onValue = { draft.date = it },
                            valid = !showErrors || draft.date.isNotBlank(),
                            modifier = Modifier.weight(3f),
                        )
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
                        style = appStyle(12.sp, FontWeight.Medium),
                        color = palette.textMuted,
                    )
                    if (showErrors && draft.date.isBlank()) {
                        Text(
                            "Choose a date",
                            modifier = Modifier.padding(top = 4.dp),
                            style = appStyle(12.sp, FontWeight.Medium),
                            color = errorInk(palette),
                        )
                    }
                } else if (draft.includeSchedule) {
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
                            style = appStyle(10.sp),
                            color = palette.danger,
                        )
                    }
                }
                Spacer(Modifier.height(22.dp))
                val buttonShape = RoundedCornerShape(12.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(buttonShape)
                        .background(saveColor.copy(alpha = if (canSubmit) 0.12f else 0.06f))
                        .border(1.5.dp, saveColor.copy(alpha = if (canSubmit) 0.40f else 0.18f), buttonShape)
                        .clickable {
                            showErrors = true
                            if (canSubmit && draft.descriptionError() == null) onSubmit()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        submitLabel,
                        style = appStyle(13.sp, FontWeight.Bold, letterSpacing = 0.04.em),
                        color = saveColor.copy(alpha = if (canSubmit) 1f else 0.4f),
                    )
                }
            }
        }
    }
}
