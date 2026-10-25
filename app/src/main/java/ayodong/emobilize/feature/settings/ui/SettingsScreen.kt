package ayodong.emobilize.feature.settings.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ayodong.emobilize.core.model.AppSettings
import ayodong.emobilize.core.model.CalView
import ayodong.emobilize.core.model.Category
import ayodong.emobilize.core.model.Priority
import ayodong.emobilize.core.model.ThemeMode
import ayodong.emobilize.core.model.WeekStart
import ayodong.emobilize.core.ui.component.FormField
import ayodong.emobilize.core.ui.form.SectionLabel
import ayodong.emobilize.core.ui.theme.AppMetrics
import ayodong.emobilize.core.ui.theme.LocalPalette
import ayodong.emobilize.core.ui.theme.lineColor
import ayodong.emobilize.core.ui.theme.ShadcnRadius
import ayodong.emobilize.core.ui.theme.appStyle
import ayodong.emobilize.core.ui.theme.monoStyle
import ayodong.emobilize.feature.settings.domain.LineDeletion
import ayodong.emobilize.feature.settings.domain.LineEdits

@Composable
fun SettingsScreen(
    appSettings: AppSettings,
    categories: List<Category>,
    onSave: (AppSettings, LineEdits) -> Unit,
    onClose: () -> Unit,
    usage: Map<Long, LineUsage> = emptyMap(),
) {
    val palette = LocalPalette.current
    var draftSettings by remember(appSettings) { mutableStateOf(appSettings) }
    var draftCategories by remember(categories) { mutableStateOf(categories.toList()) }
    var deletions by remember(categories) { mutableStateOf(emptyList<LineDeletion>()) }
    var editingCategoryId by remember { mutableStateOf(categories.firstOrNull()?.id) }
    var deletingCategory by remember { mutableStateOf<Category?>(null) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    val hasChanges = draftSettings != appSettings || draftCategories != categories || deletions.isNotEmpty()
    val duplicateCodes = draftCategories
        .groupBy { it.code.trim().uppercase() }
        .filterValues { it.size > 1 }
        .keys
    val categoriesValid = draftCategories.isNotEmpty() &&
        duplicateCodes.isEmpty() &&
        draftCategories.all { it.normalized() != null }
    val closeSettings = {
        if (hasChanges) showDiscardDialog = true else onClose()
    }
    BackHandler(enabled = !showDiscardDialog) {
        closeSettings()
    }
    Column(
        Modifier
            .fillMaxSize()
            .background(palette.surface)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = AppMetrics.headerHorizontal),
    ) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 12.dp, bottom = 20.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable(onClick = closeSettings),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = palette.text)
                }
                Text(
                    "Settings",
                    modifier = Modifier.padding(start = 8.dp),
                    style = appStyle(24.sp, FontWeight.SemiBold, lineHeight = 32.sp),
                    color = palette.text,
                )
            }

            Spacer(Modifier.height(20.dp))
            SectionLabel("APPEARANCE")
            SettingsCard {
                SettingHeading("Theme", "Choose how Emobilize looks")
                ChoiceRow {
                    ThemeMode.entries.forEach { mode ->
                        OptionChip(
                            label = mode.name,
                            selected = draftSettings.themeMode == mode,
                            onClick = { draftSettings = draftSettings.copy(themeMode = mode) },
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            SectionLabel("NOTIFICATIONS")
            SettingsCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Push notifications", style = appStyle(14.sp, FontWeight.SemiBold), color = palette.text)
                        Spacer(Modifier.height(2.dp))
                        Text(
                            if (draftSettings.notificationsEnabled) {
                                "Emobilize can alert you before a deadline"
                            } else {
                                "Reminders are paused"
                            },
                            style = appStyle(12.sp),
                            color = palette.textMuted,
                        )
                    }
                    Switch(
                        checked = draftSettings.notificationsEnabled,
                        onCheckedChange = { enabled ->
                            draftSettings = draftSettings.copy(notificationsEnabled = enabled)
                        },
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            SectionLabel("TASKS")
            SettingsCard {
                SettingHeading("Default category", "Preselect a category when adding a task")
                ChoiceRow {
                    OptionChip(
                        label = "None",
                        selected = draftSettings.defaultCategoryId == null,
                        onClick = { draftSettings = draftSettings.copy(defaultCategoryId = null) },
                    )
                    draftCategories.forEach { category ->
                        OptionChip(
                            label = category.name,
                            selected = draftSettings.defaultCategoryId == category.id,
                            onClick = { draftSettings = draftSettings.copy(defaultCategoryId = category.id) },
                        )
                    }
                }
                Spacer(Modifier.height(18.dp))
                SettingHeading("Default priority", "Preselect a priority when adding a task")
                ChoiceRow {
                    (listOf(null) + Priority.entries).forEach { priority ->
                        OptionChip(
                            label = priority?.name ?: "None",
                            selected = draftSettings.defaultPriority == priority,
                            onClick = { draftSettings = draftSettings.copy(defaultPriority = priority) },
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Show completed tasks", style = appStyle(14.sp, FontWeight.SemiBold), color = palette.text)
                        Spacer(Modifier.height(2.dp))
                        Text("Keep the Done section visible on the Tasks screen", style = appStyle(12.sp), color = palette.textMuted)
                    }
                    Switch(
                        checked = draftSettings.showCompletedTasks,
                        onCheckedChange = { draftSettings = draftSettings.copy(showCompletedTasks = it) },
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            SectionLabel("CALENDAR")
            SettingsCard {
                SettingHeading("Default view", "Choose the view shown when you open Calendar")
                ChoiceRow {
                    listOf(CalView.Week, CalView.Month).forEach { view ->
                        val stored = draftSettings.defaultCalendarView
                        OptionChip(
                            label = view.name,
                            selected = stored == view || (view == CalView.Week && stored == CalView.Day),
                            onClick = { draftSettings = draftSettings.copy(defaultCalendarView = view) },
                        )
                    }
                }
                Spacer(Modifier.height(18.dp))
                SettingHeading("Week starts on", "Use the same first day in Month and Week views")
                ChoiceRow {
                    WeekStart.entries.forEach { day ->
                        OptionChip(
                            label = day.name,
                            selected = draftSettings.weekStart == day,
                            onClick = { draftSettings = draftSettings.copy(weekStart = day) },
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            SectionLabel("LINES")
            Text(
                "Each line has a name, a 1–2 letter code and a color.",
                style = appStyle(13.sp),
                color = palette.textMuted,
            )
            if (draftCategories.any { !Category.isValidName(it.name.trim()) }) {
                Spacer(Modifier.height(6.dp))
                Text("Line names must be 1–${Category.NameLimit} characters.", style = appStyle(12.sp), color = palette.danger)
            }
            Spacer(Modifier.height(12.dp))
            draftCategories.forEach { category ->
                val sharedColor = draftCategories.filter { it.id != category.id && it.color == category.color }
                val codeClash = draftCategories.firstOrNull {
                    it.id != category.id && it.code.trim().equals(category.code.trim(), ignoreCase = true)
                }
                CategoryEditor(
                    category = category,
                    expanded = editingCategoryId == category.id,
                    codeError = codeClash?.let { "Code ${category.code.trim()} is taken by ${it.name}" },
                    colorHint = sharedColor.takeIf { it.isNotEmpty() }?.let { others ->
                        "${lineColorName(category.color)} is also used by ${others.joinToString(" and ") { it.name }}"
                    },
                    usage = usage[category.id] ?: LineUsage(),
                    canDelete = draftCategories.size > 1,
                    takenCodes = draftCategories.filter { it.id != category.id }.map { it.code }.toSet(),
                    onToggle = { editingCategoryId = if (editingCategoryId == category.id) null else category.id },
                    onCategory = { updated ->
                        draftCategories = draftCategories.map { current -> if (current.id == updated.id) updated else current }
                    },
                    onDelete = { deletingCategory = category },
                )
                Spacer(Modifier.height(12.dp))
            }
            AddLineRow {
                val taken = draftCategories.map { it.code }.toSet()
                val name = "New line"
                val color = Category.lineColors.firstOrNull { color -> draftCategories.none { it.color == color } }
                    ?: Category.lineColors.first()
                val id = (draftCategories.minOfOrNull { it.id } ?: 0L).coerceAtMost(0L) - 1L
                val line = Category(id, name, color, Category.codeFor(name, taken))
                draftCategories = draftCategories + line
                editingCategoryId = line.id
            }
        }

        if (hasChanges) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(palette.surface)
                    .padding(top = 8.dp, bottom = 12.dp),
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(ShadcnRadius.lg))
                        .background(if (categoriesValid) palette.scheme.primary else palette.scheme.muted)
                        .clickable(enabled = categoriesValid) { onSave(draftSettings, LineEdits(draftCategories, deletions)) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "Save Changes",
                        style = appStyle(14.sp, FontWeight.Bold),
                        color = if (categoriesValid) palette.scheme.primaryForeground else palette.textMuted,
                    )
                }
            }
        }
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Discard changes?") },
            text = { Text("Your unsaved settings changes will be lost.") },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardDialog = false
                    onClose()
                }) { Text("Discard") }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) { Text("Keep editing") }
            },
        )
    }

    deletingCategory?.let { line ->
        DeleteLineDialog(
            line = line,
            usage = usage[line.id] ?: LineUsage(),
            remaining = draftCategories.filter { it.id != line.id },
            onDismiss = { deletingCategory = null },
            onConfirm = { replacement ->
                draftCategories = draftCategories.filter { it.id != line.id }
                if (!LineEdits.isNew(line)) deletions = deletions + LineDeletion(line.id, replacement?.id)
                if (draftSettings.defaultCategoryId == line.id) draftSettings = draftSettings.copy(defaultCategoryId = null)
                if (editingCategoryId == line.id) editingCategoryId = null
                deletingCategory = null
            },
        )
    }
}

/** Colour names in [Category.lineColors] order, for the shared-colour hint. */
private val lineColorNames = listOf(
    "Red", "Blue", "Green", "Purple", "Orange", "Pink", "Teal", "Brown", "Navy", "Lime", "Magenta", "Grey",
)

private fun lineColorName(color: Int): String =
    lineColorNames.getOrNull(Category.lineColors.indexOf(color)) ?: "This color"

private fun stopsLabel(usage: LineUsage): String = when (usage.stops) {
    0 -> "No stops yet"
    1 -> "Used by 1 stop"
    else -> "Used by ${usage.stops} stops"
}

/** The station ring from the board: line colour behind a white code. */
@Composable
private fun LineRing(category: Category, modifier: Modifier = Modifier, size: Dp = 28.dp) {
    Box(
        modifier
            .size(size)
            .clip(CircleShape)
            .background(lineColor(category.color)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            category.code.trim().ifEmpty { "?" },
            style = monoStyle(if (category.code.trim().length > 1) 11.sp else 13.sp),
            color = Color.White,
            maxLines = 1,
        )
    }
}

@Composable
private fun AddLineRow(onAdd: () -> Unit) {
    val palette = LocalPalette.current
    val shape = RoundedCornerShape(ShadcnRadius.lg)
    val dash = palette.textMuted
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .drawBehind {
                drawRoundRect(
                    color = dash,
                    style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))),
                    cornerRadius = CornerRadius(ShadcnRadius.lg.toPx()),
                )
            }
            .clickable(onClick = onAdd)
            .padding(start = 16.dp, end = 6.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .padding(end = 14.dp)
                .size(28.dp)
                .border(1.dp, dash, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, tint = dash, modifier = Modifier.size(16.dp))
        }
        Text("Add line", style = appStyle(15.sp, FontWeight.SemiBold), color = palette.textMuted)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DeleteLineDialog(
    line: Category,
    usage: LineUsage,
    remaining: List<Category>,
    onDismiss: () -> Unit,
    onConfirm: (replacement: Category?) -> Unit,
) {
    val palette = LocalPalette.current
    val needsReplacement = usage.blocks > 0
    var replacement by remember(line.id) { mutableStateOf(remaining.firstOrNull()) }
    val message = buildString {
        append(stopsLabel(usage))
        append(". ")
        when {
            usage.stops == 0 -> append("Nothing moves.")
            else -> {
                if (usage.tasks > 0) append("${usage.tasks} ${if (usage.tasks == 1) "task loses" else "tasks lose"} their line. ")
                if (needsReplacement) append("${usage.blocks} time ${if (usage.blocks == 1) "block moves" else "blocks move"} to:")
            }
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete ${line.name.trim()} line?") },
        text = {
            Column {
                Text(message.trim())
                if (needsReplacement) {
                    Spacer(Modifier.height(12.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        remaining.forEach { candidate ->
                            val selected = replacement?.id == candidate.id
                            val shape = RoundedCornerShape(ShadcnRadius.md)
                            Row(
                                Modifier
                                    .clip(shape)
                                    .background(if (selected) palette.scheme.primary else Color.Transparent)
                                    .border(1.dp, if (selected) palette.scheme.primary else palette.scheme.border, shape)
                                    .clickable { replacement = candidate }
                                    .padding(start = 8.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                LineRing(candidate, size = 20.dp)
                                Text(
                                    candidate.name.trim(),
                                    style = appStyle(12.sp, FontWeight.SemiBold),
                                    color = if (selected) palette.scheme.primaryForeground else palette.textMuted,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !needsReplacement || replacement != null,
                onClick = { onConfirm(if (needsReplacement) replacement else null) },
            ) { Text("Delete", color = palette.danger) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@Composable
private fun SectionLabel(text: String) {
    val palette = LocalPalette.current
    Text(
        text,
        modifier = Modifier.padding(start = 2.dp, bottom = 8.dp),
        style = appStyle(11.sp, FontWeight.Bold, letterSpacing = 0.1.em),
        color = palette.textMuted,
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    val palette = LocalPalette.current
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppMetrics.cardRadius))
            .background(palette.card)
            .border(1.dp, palette.scheme.border, RoundedCornerShape(AppMetrics.cardRadius))
            .padding(14.dp),
    ) { content() }
}

@Composable
private fun SettingHeading(title: String, description: String) {
    val palette = LocalPalette.current
    Text(title, style = appStyle(14.sp, FontWeight.SemiBold), color = palette.text)
    Spacer(Modifier.height(2.dp))
    Text(description, style = appStyle(12.sp), color = palette.textMuted)
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun ChoiceRow(content: @Composable () -> Unit) {
    Row(
        Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) { content() }
}

@Composable
private fun OptionChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val palette = LocalPalette.current
    val shape = RoundedCornerShape(ShadcnRadius.md)
    Box(
        Modifier
            .clip(shape)
            .background(if (selected) palette.scheme.primary else Color.Transparent)
            .border(1.dp, if (selected) palette.scheme.primary else palette.scheme.border, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = appStyle(12.sp, FontWeight.SemiBold),
            color = if (selected) palette.scheme.primaryForeground else palette.textMuted,
            maxLines = 1,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryEditor(
    category: Category,
    expanded: Boolean,
    codeError: String?,
    colorHint: String?,
    usage: LineUsage,
    canDelete: Boolean,
    takenCodes: Set<String>,
    onToggle: () -> Unit,
    onCategory: (Category) -> Unit,
    onDelete: () -> Unit,
) {
    val palette = LocalPalette.current
    var name by remember(category.id) { mutableStateOf(category.name) }
    var code by remember(category.id) { mutableStateOf(category.code) }
    // A new line's code follows its name until the user types a code of their own.
    var codeFollowsName by remember(category.id) { mutableStateOf(LineEdits.isNew(category)) }
    val shape = RoundedCornerShape(ShadcnRadius.lg)
    if (expanded) {
        SettingsCard {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
                FormField(
                    value = name,
                    onValueChange = { next ->
                        name = next
                        if (codeFollowsName && next.isNotBlank()) {
                            code = Category.codeFor(next, takenCodes)
                            onCategory(category.copy(name = next, code = code))
                        } else {
                            onCategory(category.copy(name = next))
                        }
                    },
                    label = "Line name",
                    error = if (name.isBlank()) "Enter a line name" else null,
                    keyboardType = KeyboardType.Text,
                    modifier = Modifier.weight(1f),
                )
                FormField(
                    value = code,
                    onValueChange = { next ->
                        val cleaned = next.filterNot { it.isWhitespace() || it.isISOControl() }.take(Category.CodeLimit)
                        codeFollowsName = false
                        code = cleaned
                        onCategory(category.copy(code = cleaned))
                    },
                    label = "Code",
                    error = when {
                        code.isEmpty() -> "1–2 letters"
                        codeError != null -> "Taken"
                        else -> null
                    },
                    keyboardType = KeyboardType.Text,
                    modifier = Modifier.width(88.dp),
                    imeAction = ImeAction.Done,
                )
            }
            if (codeError != null) {
                Spacer(Modifier.height(4.dp))
                Text(codeError, style = appStyle(12.sp, FontWeight.Medium), color = palette.danger)
            }
            Spacer(Modifier.height(12.dp))
            Text("Color", style = appStyle(13.sp, FontWeight.Medium), color = palette.textMuted)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                Category.lineColors.forEach { color ->
                    val selected = category.color == color
                    Box(
                        Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .clickable { onCategory(category.copy(color = color)) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(lineColor(color))
                                .border(if (selected) 2.dp else 1.dp, if (selected) palette.text else palette.scheme.border, CircleShape),
                        )
                    }
                }
            }
            if (colorHint != null) {
                Spacer(Modifier.height(4.dp))
                Text(colorHint, style = appStyle(12.sp), color = palette.textMuted)
            }
            Spacer(Modifier.height(12.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(palette.scheme.border))
            Row(
                Modifier.fillMaxWidth().padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(ShadcnRadius.md))
                        .clickable(enabled = canDelete, onClick = onDelete)
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                ) {
                    Text(
                        if (canDelete) "Delete line" else "Keep at least one line",
                        style = appStyle(13.sp, FontWeight.SemiBold),
                        color = if (canDelete) palette.danger else palette.textMuted,
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(stopsLabel(usage), style = appStyle(12.sp), color = palette.textMuted)
            }
        }
    } else {
        Row(
            Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(palette.card)
                .border(1.dp, palette.scheme.border, shape)
                .clickable(onClick = onToggle)
                .padding(start = 16.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LineRing(category, Modifier.padding(end = 14.dp))
            Text(
                category.name.trim().ifEmpty { "Unnamed line" },
                modifier = Modifier.weight(1f),
                style = appStyle(15.sp, FontWeight.SemiBold),
                color = palette.text,
            )
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onToggle),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit ${category.name}", tint = palette.textMuted, modifier = Modifier.size(18.dp))
            }
        }
    }
}
