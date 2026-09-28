package ayodong.emobilize.ui.tasks

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ayodong.emobilize.model.Category
import ayodong.emobilize.model.Priority
import ayodong.emobilize.model.ScheduleMode
import ayodong.emobilize.model.formatInputTime
import ayodong.emobilize.ui.theme.AppFont
import ayodong.emobilize.ui.theme.LocalPalette
import ayodong.emobilize.ui.theme.ShadcnRadius
import ayodong.emobilize.ui.theme.appStyle
import ayodong.emobilize.ui.theme.at
import ayodong.emobilize.ui.theme.categoryColor
import ayodong.emobilize.ui.theme.inkOn
import ayodong.emobilize.ui.theme.priorityColor
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskFormSheet(
    title: String,
    submitLabel: String,
    draft: TaskDraft,
    canSubmit: Boolean,
    saveColor: Color,
    saveContent: Color,
    disabledColor: Color,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
) {
    val palette = LocalPalette.current
    val shape = RoundedCornerShape(topStart = ShadcnRadius.xl, topEnd = ShadcnRadius.xl)
    val scrimSource = remember { MutableInteractionSource() }
    val sheetSource = remember { MutableInteractionSource() }
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
                DraftField(
                    value = draft.name,
                    onValue = { draft.name = it },
                    placeholder = "Task name...",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                )
                Spacer(Modifier.height(14.dp))
                SectionLabel("Category")
                FlowRow(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Category.entries.forEach { category ->
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
                        val active = draft.scheduleMode == mode
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
                                .clickable { draft.scheduleMode = mode },
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
                if (draft.scheduleMode == ScheduleMode.Deadline) {
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        DateField(
                            value = draft.date,
                            onValue = { draft.date = it },
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
                        style = appStyle(10.sp),
                        color = palette.textMuted,
                    )
                } else {
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (canSubmit) saveColor else disabledColor)
                        .clickable(enabled = canSubmit, onClick = onSubmit),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        submitLabel,
                        style = appStyle(14.sp, FontWeight.Bold, letterSpacing = 0.04.em),
                        color = if (canSubmit) saveContent else saveContent.copy(alpha = 0.7f),
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = appStyle(10.5.sp, FontWeight.Bold, letterSpacing = 0.1.em),
        color = LocalPalette.current.textMuted,
    )
}

@Composable
private fun DraftField(
    value: String,
    onValue: (String) -> Unit,
    placeholder: String,
    modifier: Modifier,
) {
    val palette = LocalPalette.current
    BasicTextField(
        value = value,
        onValueChange = onValue,
        singleLine = true,
        textStyle = TextStyle(
            fontFamily = AppFont,
            color = palette.text,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        cursorBrush = SolidColor(palette.primary),
        modifier = modifier
            .clip(RoundedCornerShape(ShadcnRadius.lg))
            .background(palette.scheme.background)
            .border(1.dp, palette.scheme.input, RoundedCornerShape(ShadcnRadius.lg))
            .padding(horizontal = 14.dp),
        decorationBox = { inner ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty()) {
                    Text(placeholder, style = appStyle(15.sp, FontWeight.SemiBold), color = palette.textMuted)
                }
                inner()
            }
        },
    )
}

@Composable
private fun RangeRow(
    label: String,
    date: String,
    time: String,
    minDate: String?,
    rangeValid: Boolean,
    onChange: (String, String) -> Unit,
) {
    val palette = LocalPalette.current
    Row(
        modifier = Modifier.padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, modifier = Modifier.width(34.dp), style = appStyle(9.5.sp, FontWeight.Bold), color = palette.textMuted)
        DateField(
            value = date,
            onValue = { onChange(it, time) },
            minIso = minDate,
            valid = rangeValid,
            modifier = Modifier.weight(3f),
        )
        Spacer(Modifier.width(7.dp))
        TimeField(
            value = time,
            onValue = { onChange(date, it) },
            allowClear = false,
            valid = rangeValid,
            modifier = Modifier.weight(2f),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateField(
    value: String,
    onValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    minIso: String? = null,
    valid: Boolean = true,
) {
    val palette = LocalPalette.current
    var open by remember { mutableStateOf(false) }
    val today = LocalDate.now()
    val minDate = minIso?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: today
    Box(
        modifier
            .height(44.dp)
            .clip(RoundedCornerShape(ShadcnRadius.lg))
            .background(palette.scheme.background)
            .border(1.dp, if (valid) palette.scheme.input else palette.scheme.destructive, RoundedCornerShape(ShadcnRadius.lg))
            .clickable { open = true }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = value.ifBlank { "Date" },
            style = appStyle(13.sp, FontWeight.SemiBold),
            color = if (value.isBlank()) palette.textMuted else palette.text,
            maxLines = 1,
        )
    }
    if (open) {
        val initial = runCatching { LocalDate.parse(value) }.getOrDefault(today)
        val state = rememberDatePickerState(
            initialSelectedDateMillis = initial.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val date = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneOffset.UTC).toLocalDate()
                    return !date.isBefore(minDate)
                }
            },
        )
        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        onValue(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate().toString())
                    }
                    open = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { open = false }) { Text("Cancel") } },
        ) {
            DatePicker(state = state)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeField(
    value: String,
    onValue: (String) -> Unit,
    allowClear: Boolean,
    modifier: Modifier = Modifier,
    valid: Boolean = true,
) {
    val palette = LocalPalette.current
    var open by remember { mutableStateOf(false) }
    Box(
        modifier
            .height(44.dp)
            .clip(RoundedCornerShape(ShadcnRadius.lg))
            .background(palette.scheme.background)
            .border(1.dp, if (valid) palette.scheme.input else palette.scheme.destructive, RoundedCornerShape(ShadcnRadius.lg))
            .clickable { open = true }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        val label = if (value.isBlank()) "Time" else formatInputTime(value)
        Text(
            text = label,
            style = appStyle(13.sp, FontWeight.SemiBold),
            color = if (value.isBlank()) palette.textMuted else palette.text,
            maxLines = 1,
        )
    }
    if (open) {
        val parts = value.split(":")
        val state = rememberTimePickerState(
            initialHour = parts.getOrNull(0)?.toIntOrNull() ?: 9,
            initialMinute = parts.getOrNull(1)?.toIntOrNull() ?: 0,
            is24Hour = false,
        )
        Dialog(onDismissRequest = { open = false }) {
            Column(
                Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(palette.card)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TimePicker(state = state)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    if (allowClear) {
                        TextButton(onClick = {
                            onValue("")
                            open = false
                        }) { Text("Clear") }
                    }
                    TextButton(onClick = { open = false }) { Text("Cancel") }
                    TextButton(onClick = {
                        onValue("%02d:%02d".format(state.hour, state.minute))
                        open = false
                    }) { Text("OK") }
                }
            }
        }
    }
}
