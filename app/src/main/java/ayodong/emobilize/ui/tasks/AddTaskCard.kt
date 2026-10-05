package ayodong.emobilize.ui.tasks

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ayodong.emobilize.domain.model.Category
import ayodong.emobilize.domain.model.Priority
import ayodong.emobilize.domain.model.ScheduleMode
import ayodong.emobilize.ui.theme.AppFont
import ayodong.emobilize.ui.theme.LocalPalette
import ayodong.emobilize.ui.theme.ShadcnRadius
import ayodong.emobilize.ui.theme.appStyle
import ayodong.emobilize.ui.theme.at
import ayodong.emobilize.ui.theme.categoryColor
import ayodong.emobilize.ui.theme.inkOn
import ayodong.emobilize.ui.theme.priorityColor

private enum class AddOption { Category, Priority, Schedule }

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddTaskCard(
    draft: TaskDraft,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit,
) {
    val palette = LocalPalette.current
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val scrimSource = remember { MutableInteractionSource() }
    val cardSource = remember { MutableInteractionSource() }
    var option by remember { mutableStateOf<AddOption?>(null) }
    val shape = RoundedCornerShape(16.dp)

    fun close() {
        val rangeBlocked = draft.includeSchedule &&
            draft.scheduleMode == ScheduleMode.Range &&
            !draft.rangeValid()
        when {
            draft.name.isBlank() -> onDismiss()
            rangeBlocked -> option = AddOption.Schedule
            else -> onSubmit()
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboard?.show()
    }
    BackHandler(onBack = ::close)

    Box(
        Modifier
            .fillMaxSize()
            .background(palette.modalBg)
            .imePadding()
            .navigationBarsPadding()
            .clickable(interactionSource = scrimSource, indication = null, onClick = ::close),
    ) {
        BoxWithConstraints(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .clickable(interactionSource = cardSource, indication = null) {},
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxHeight)
                    .clip(shape)
                    .background(palette.scheme.popover)
                    .border(1.dp, palette.scheme.border, shape)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 8.dp, top = 14.dp, bottom = 6.dp),
            ) {
                BasicTextField(
                    value = draft.name,
                    onValueChange = { draft.name = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontFamily = AppFont,
                        color = palette.text,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                    cursorBrush = SolidColor(palette.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp, bottom = 8.dp)
                        .focusRequester(focusRequester),
                    decorationBox = { inner ->
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                            if (draft.name.isEmpty()) {
                                Text("Title", style = appStyle(16.sp), color = palette.textMuted)
                            }
                            inner()
                        }
                    },
                )
                when (option) {
                    AddOption.Category -> CategoryOptions(draft)
                    AddOption.Priority -> PriorityOptions(draft)
                    AddOption.Schedule -> ScheduleOptions(draft)
                    null -> Unit
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OptionButton(
                        label = "Category",
                        tint = draft.category?.let { palette.categoryColor(it) }
                            ?: if (option == AddOption.Category) palette.text else palette.textMuted,
                        onClick = { option = if (option == AddOption.Category) null else AddOption.Category },
                    ) { color -> CategoryMark(color) }
                    OptionButton(
                        label = "Priority",
                        tint = draft.priority?.let { palette.priorityColor(it) }
                            ?: if (option == AddOption.Priority) palette.text else palette.textMuted,
                        onClick = { option = if (option == AddOption.Priority) null else AddOption.Priority },
                    ) { color -> PriorityMark(color) }
                    OptionButton(
                        label = "Schedule",
                        tint = if (scheduleChosen(draft) || option == AddOption.Schedule) palette.text else palette.textMuted,
                        onClick = { option = if (option == AddOption.Schedule) null else AddOption.Schedule },
                    ) { color ->
                        Icon(Icons.Filled.DateRange, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.weight(1f))
                    Text(
                        "Close",
                        modifier = Modifier
                            .heightIn(min = 44.dp)
                            .clip(RoundedCornerShape(ShadcnRadius.lg))
                            .clickable(onClick = ::close)
                            .padding(horizontal = 10.dp, vertical = 12.dp),
                        style = appStyle(15.sp, FontWeight.Medium),
                        color = palette.text,
                    )
                }
            }
        }
    }
}

private fun scheduleChosen(draft: TaskDraft): Boolean = when {
    !draft.includeSchedule -> false
    draft.scheduleMode == ScheduleMode.Deadline -> draft.date.isNotBlank()
    else -> draft.rangeValid()
}

@Composable
private fun OptionButton(
    label: String,
    tint: Color,
    onClick: () -> Unit,
    icon: @Composable (Color) -> Unit,
) {
    Box(
        Modifier
            .size(44.dp)
            .semantics { contentDescription = label }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        icon(tint)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryOptions(draft: TaskDraft) {
    val palette = LocalPalette.current
    FlowRow(
        modifier = Modifier.padding(bottom = 8.dp),
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
                    .clickable { draft.category = if (selected) null else category }
                    .padding(horizontal = 12.dp, vertical = 5.dp),
                style = appStyle(11.5.sp, FontWeight.Bold),
                color = if (selected) palette.inkOn(color) else color,
            )
        }
    }
}

@Composable
private fun PriorityOptions(draft: TaskDraft) {
    val palette = LocalPalette.current
    Row(
        modifier = Modifier.padding(bottom = 8.dp),
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
                    .clickable { draft.priority = if (selected) null else level }
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
}

@Composable
private fun ScheduleOptions(draft: TaskDraft) {
    val palette = LocalPalette.current
    Column(Modifier.padding(bottom = 8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            if (active) {
                                draft.includeSchedule = false
                            } else {
                                draft.includeSchedule = true
                                draft.scheduleMode = mode
                            }
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
                DateField(value = draft.date, onValue = { draft.date = it }, modifier = Modifier.weight(3f))
                TimeField(
                    value = draft.time,
                    onValue = { draft.time = it },
                    allowClear = true,
                    modifier = Modifier.weight(2f),
                )
            }
            Text(
                "Date and time are optional",
                modifier = Modifier.padding(top = 5.dp),
                style = appStyle(10.sp),
                color = palette.textMuted,
            )
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
    }
}

@Composable
private fun CategoryMark(color: Color) {
    Canvas(Modifier.size(22.dp)) {
        val stroke = 1.6.dp.toPx()
        drawCircle(color = color, style = Stroke(width = stroke))
        drawCircle(color = color, radius = size.minDimension * 0.28f)
    }
}

@Composable
private fun PriorityMark(color: Color) {
    Canvas(Modifier.size(22.dp)) {
        val gap = size.width * 0.16f
        val bar = size.width * 0.18f
        listOf(0.42f, 0.7f, 1f).forEachIndexed { index, height ->
            val left = index * (bar + gap)
            val top = size.height * (1f - height)
            drawRoundRect(
                color = color,
                topLeft = Offset(left, top),
                size = Size(bar, size.height - top),
                cornerRadius = CornerRadius(bar / 2f, bar / 2f),
            )
        }
    }
}
