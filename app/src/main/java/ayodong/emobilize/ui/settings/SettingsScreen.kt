package ayodong.emobilize.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ayodong.emobilize.domain.model.AppSettings
import ayodong.emobilize.domain.model.CalView
import ayodong.emobilize.domain.model.Category
import ayodong.emobilize.domain.model.Priority
import ayodong.emobilize.domain.model.ThemeMode
import ayodong.emobilize.domain.model.WeekStart
import ayodong.emobilize.ui.theme.AppFont
import ayodong.emobilize.ui.theme.AppMetrics
import ayodong.emobilize.ui.theme.LocalPalette
import ayodong.emobilize.ui.theme.ShadcnRadius
import ayodong.emobilize.ui.theme.appStyle

@Composable
fun SettingsScreen(
    appSettings: AppSettings,
    categories: List<Category>,
    onSave: (AppSettings, List<Category>) -> Unit,
    onClose: () -> Unit,
) {
    val palette = LocalPalette.current
    var draftSettings by remember(appSettings) { mutableStateOf(appSettings) }
    var draftCategories by remember(categories) { mutableStateOf(categories.toList()) }
    var editingCategoryId by remember { mutableStateOf(categories.firstOrNull()?.id) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    val hasChanges = draftSettings != appSettings || draftCategories != categories
    val categoriesValid = draftCategories.all { category ->
        val name = category.name.trim()
        name.isNotEmpty() && name.length <= 24 && name.none { it == '\u001E' || it == '\u001F' }
    }
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
                    listOf(CalView.Week, CalView.Day, CalView.Month).forEach { view ->
                        OptionChip(
                            label = view.name,
                            selected = draftSettings.defaultCalendarView == view,
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
            SectionLabel("CATEGORIES")
            Text(
                "Edit up to ${Category.Limit} categories and choose a color for each.",
                style = appStyle(13.sp),
                color = palette.textMuted,
            )
            if (!categoriesValid) {
                Spacer(Modifier.height(6.dp))
                Text("Category names must be 1–24 characters.", style = appStyle(12.sp), color = palette.danger)
            }
            Spacer(Modifier.height(12.dp))
            draftCategories.forEach { category ->
                CategoryEditor(
                    category = category,
                    expanded = editingCategoryId == category.id,
                    onToggle = { editingCategoryId = if (editingCategoryId == category.id) null else category.id },
                    onCategory = { updated ->
                        draftCategories = draftCategories.map { current -> if (current.id == updated.id) updated else current }
                    },
                )
                Spacer(Modifier.height(12.dp))
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
                        .clickable(enabled = categoriesValid) { onSave(draftSettings, draftCategories) },
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
}

@Composable
private fun SectionLabel(text: String) {
    val palette = LocalPalette.current
    Text(
        text,
        modifier = Modifier.padding(start = 2.dp, bottom = 8.dp),
        style = appStyle(11.sp, FontWeight.Bold, letterSpacing = 0.1.sp),
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

@Composable
private fun CategoryEditor(
    category: Category,
    expanded: Boolean,
    onToggle: () -> Unit,
    onCategory: (Category) -> Unit,
) {
    val palette = LocalPalette.current
    var name by remember(category.id, category.name) { mutableStateOf(category.name) }
    val shape = RoundedCornerShape(ShadcnRadius.lg)
    if (expanded) {
        SettingsCard {
            BasicTextField(
                value = name,
                onValueChange = { next ->
                    name = next
                    onCategory(category.copy(name = next))
                },
                singleLine = true,
                textStyle = TextStyle(
                    fontFamily = AppFont,
                    color = palette.text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                cursorBrush = SolidColor(palette.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(shape)
                    .background(palette.scheme.background)
                    .border(1.dp, palette.scheme.input, shape)
                    .padding(horizontal = 12.dp),
                decorationBox = { inner ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                        if (name.isEmpty()) {
                            Text("Category name", style = appStyle(16.sp, FontWeight.Medium), color = palette.textMuted)
                        }
                        inner()
                    }
                },
            )
            Spacer(Modifier.height(12.dp))
            Text("Color", style = appStyle(13.sp, FontWeight.Medium), color = palette.textMuted)
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                Category.colorChoices.forEach { color ->
                    val selected = category.color == color
                    Box(
                        Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .clickable { onCategory(category.copy(name = name, color = color)) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(color))
                                .border(if (selected) 2.dp else 1.dp, if (selected) palette.text else palette.scheme.border, CircleShape),
                        )
                    }
                }
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
            Box(
                Modifier
                    .padding(end = 14.dp)
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(Color(category.color)),
            )
            Text(category.name, modifier = Modifier.weight(1f), style = appStyle(15.sp, FontWeight.SemiBold), color = palette.text)
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
