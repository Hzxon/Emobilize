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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import ayodong.emobilize.model.Category
import ayodong.emobilize.model.EventType
import ayodong.emobilize.model.Priority
import ayodong.emobilize.model.ScheduleMode
import ayodong.emobilize.ui.theme.AppFont
import ayodong.emobilize.ui.theme.LocalPalette
import ayodong.emobilize.ui.theme.ShadcnRadius
import ayodong.emobilize.ui.theme.appStyle
import ayodong.emobilize.ui.theme.at
import ayodong.emobilize.ui.theme.categoryColor
import ayodong.emobilize.ui.theme.inkOn
import ayodong.emobilize.ui.theme.priorityColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalendarFormSheet(
    draft: TaskDraft,
    lockType: Boolean,
    canSubmit: Boolean,
    saveColor: Color,
    submitLabel: String,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
) {
    val palette = LocalPalette.current
    val shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val scrimSource = remember { MutableInteractionSource() }
    val sheetSource = remember { MutableInteractionSource() }
    val types = if (lockType) listOf(draft.eventType) else EventType.entries
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
                    .padding(start = 22.dp, end = 22.dp, top = 20.dp, bottom = 36.dp),
            ) {
                Box(
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(36.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(palette.textMuted.copy(alpha = 0.25f)),
                )
                Spacer(Modifier.height(18.dp))
                BasicTextField(
                    value = draft.name,
                    onValueChange = { draft.name = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontFamily = AppFont,
                        color = palette.text,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    cursorBrush = SolidColor(palette.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    decorationBox = { inner ->
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                            if (draft.name.isEmpty()) {
                                Text(
                                    "Add title",
                                    style = appStyle(22.sp, FontWeight.Bold),
                                    color = palette.textMuted,
                                )
                            }
                            inner()
                        }
                    },
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.primary.copy(alpha = 0.06f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    types.forEach { type ->
                        val active = draft.eventType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(9.dp))
                                .background(if (active) palette.primary.copy(alpha = 0.12f) else Color.Transparent)
                                .clickable(enabled = !lockType) { draft.eventType = type }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                type.label,
                                style = appStyle(11.sp, if (active) FontWeight.Bold else FontWeight.Medium),
                                color = if (active) palette.primaryDark else palette.textMuted,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(22.dp))
                when (draft.eventType) {
                    EventType.Event -> EventFields(draft)
                    EventType.Task -> TaskFields(draft)
                    EventType.RegularSchedule -> ScheduleFields(draft)
                }
                Spacer(Modifier.height(22.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlineAction(
                        label = "Cancel",
                        color = palette.textMuted,
                        enabled = true,
                        modifier = Modifier.weight(1f),
                        onClick = onDismiss,
                    )
                    OutlineAction(
                        label = submitLabel,
                        color = saveColor,
                        enabled = canSubmit,
                        modifier = Modifier.weight(1.45f),
                        onClick = onSubmit,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EventFields(draft: TaskDraft) {
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
            placeholder = "Start",
            modifier = Modifier.weight(1f),
        )
        TimeField(
            value = draft.endTime,
            onValue = { draft.endTime = it },
            allowClear = true,
            placeholder = "End",
            modifier = Modifier.weight(1f),
        )
    }
    Spacer(Modifier.height(18.dp))
    CategoryPicker(draft)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TaskFields(draft: TaskDraft) {
    val palette = LocalPalette.current
    CategoryPicker(draft)
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
                    .background(if (active) palette.primary.copy(alpha = 0.12f) else Color.Transparent)
                    .border(
                        1.5.dp,
                        if (active) palette.primary else palette.primary.copy(alpha = 0.2f),
                        RoundedCornerShape(ShadcnRadius.md),
                    )
                    .clickable { draft.scheduleMode = mode },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    label,
                    style = appStyle(11.5.sp, FontWeight.Bold),
                    color = if (active) palette.primaryDark else palette.textMuted,
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
            style = appStyle(10.sp),
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
            style = appStyle(10.sp),
            color = palette.danger,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryPicker(draft: TaskDraft) {
    val palette = LocalPalette.current
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
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (selected) color else color.at(0x18))
                    .clickable { draft.category = category }
                    .padding(horizontal = 12.dp, vertical = 5.dp),
                style = appStyle(11.5.sp, FontWeight.Bold),
                color = if (selected) palette.inkOn(color) else color,
            )
        }
    }
}

@Composable
private fun OutlineAction(
    label: String,
    color: Color,
    enabled: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(shape)
            .background(color.copy(alpha = if (enabled) 0.12f else 0.06f))
            .border(1.5.dp, color.copy(alpha = if (enabled) 0.40f else 0.18f), shape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = appStyle(13.sp, FontWeight.Bold, letterSpacing = 0.04.em),
            color = color.copy(alpha = if (enabled) 1f else 0.4f),
        )
    }
}
