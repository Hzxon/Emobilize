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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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
import ayodong.emobilize.core.ui.theme.appStyle
import ayodong.emobilize.core.ui.theme.at
import ayodong.emobilize.core.ui.theme.categoryColor
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
    val shape = RectangleShape
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, style = appStyle(18.sp, FontWeight.ExtraBold, letterSpacing = (-0.5).sp), color = palette.text)
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
                        .border(2.dp, palette.text, RectangleShape),
                ) {
                    ScheduleMode.entries.forEachIndexed { index, mode ->
                        val active = draft.includeSchedule && draft.scheduleMode == mode
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
                                    onClick = {
                                        draft.includeSchedule = true
                                        draft.scheduleMode = mode
                                    }
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
                            style = appStyle(11.sp),
                            color = palette.danger,
                        )
                    }
                }
                Spacer(Modifier.height(28.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .background(palette.card)
                            .border(2.dp, palette.text, RectangleShape)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "CANCEL",
                            style = appStyle(13.sp, FontWeight.ExtraBold, letterSpacing = 1.sp),
                            color = palette.text,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1.4f)
                            .height(52.dp)
                            .background(if (canSubmit) palette.primary else palette.scheme.muted)
                            .border(2.dp, if (canSubmit) palette.primary else palette.scheme.border, RectangleShape)
                            // Stays tappable while invalid: the tap is what reveals the field errors.
                            .clickable {
                                showErrors = true
                                if (canSubmit && draft.descriptionError() == null) onSubmit()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            submitLabel.uppercase(),
                            style = appStyle(13.sp, FontWeight.ExtraBold, letterSpacing = 1.sp),
                            color = if (canSubmit) palette.onFilled else palette.textMuted,
                        )
                    }
                }
            }
        }
    }
}
