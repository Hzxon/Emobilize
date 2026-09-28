package ayodong.emobilize.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import ayodong.emobilize.model.NavTab
import ayodong.emobilize.ui.dock.BottomDock
import ayodong.emobilize.ui.dock.RadialMenu
import ayodong.emobilize.ui.dock.ThemeOrb
import ayodong.emobilize.ui.tasks.TaskFormSheet
import ayodong.emobilize.ui.tasks.TasksScreen
import ayodong.emobilize.ui.tasks.TasksViewModel
import ayodong.emobilize.ui.theme.EmobilizeTheme
import ayodong.emobilize.ui.theme.LocalPalette
import ayodong.emobilize.ui.theme.NeutralDark
import ayodong.emobilize.ui.theme.NeutralLight
import ayodong.emobilize.ui.theme.appStyle
import ayodong.emobilize.ui.theme.at
import ayodong.emobilize.ui.theme.inkOn

@Composable
fun EmobilizeApp(viewModel: TasksViewModel = viewModel()) {
    var themeId by rememberSaveable { mutableIntStateOf(1) }
    var tabName by rememberSaveable { mutableStateOf(NavTab.Tasks.name) }
    var menuOpen by rememberSaveable { mutableStateOf(false) }
    val tab = if (tabName == NavTab.Calendar.name) NavTab.Calendar else NavTab.Tasks
    val palette = if (themeId == 1) NeutralLight else NeutralDark

    EmobilizeTheme(palette) {
        val colors = LocalPalette.current
        BackHandler(enabled = viewModel.showAdd || viewModel.showEdit || menuOpen || viewModel.pendingAction != null) {
            when {
                viewModel.showAdd -> viewModel.dismissAdd()
                viewModel.showEdit -> viewModel.dismissEdit()
                menuOpen -> menuOpen = false
                else -> viewModel.cancelPending()
            }
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(colors.surface),
        ) {
            Column(Modifier.fillMaxSize()) {
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .statusBarsPadding(),
                ) {
                    if (tab == NavTab.Tasks) {
                        TasksScreen(viewModel)
                    } else {
                        CalendarPlaceholder()
                    }
                }
                BottomDock(
                    tab = tab,
                    onTab = { next ->
                        tabName = next.name
                        menuOpen = false
                    },
                )
            }
            RadialMenu(
                tab = tab,
                menuOpen = menuOpen,
                onMenuOpenChange = { menuOpen = it },
                onAction = { action ->
                    menuOpen = false
                    if (tab != NavTab.Tasks) tabName = NavTab.Tasks.name
                    viewModel.onFabAction(action)
                },
                onFilter = { key ->
                    menuOpen = false
                    tabName = NavTab.Tasks.name
                    viewModel.cancelPending()
                    viewModel.selectFilter(key)
                },
                modifier = Modifier.zIndex(2f),
            )
            ThemeOrb(
                themeId = themeId,
                onClick = { themeId = if (themeId == 1) 2 else 1 },
                modifier = Modifier
                    .zIndex(3f)
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 6.dp, end = 18.dp),
            )
            if (viewModel.showAdd && tab == NavTab.Tasks) {
                Box(Modifier.zIndex(4f)) {
                    TaskFormSheet(
                        title = "New Task",
                        submitLabel = "Add Task",
                        draft = viewModel.addDraft,
                        canSubmit = viewModel.canSubmit(viewModel.addDraft),
                        saveColor = colors.primary,
                        saveContent = colors.onFilled,
                        disabledColor = colors.primary.at(0x40),
                        onDismiss = viewModel::dismissAdd,
                        onSubmit = viewModel::submitAdd,
                    )
                }
            }
            if (viewModel.showEdit) {
                Box(Modifier.zIndex(4f)) {
                    TaskFormSheet(
                        title = "Edit Task",
                        submitLabel = "Save Changes",
                        draft = viewModel.editDraft,
                        canSubmit = viewModel.canSubmit(viewModel.editDraft),
                        saveColor = colors.primaryDark,
                        saveContent = colors.inkOn(colors.primaryDark),
                        disabledColor = colors.primary.at(0x40),
                        onDismiss = viewModel::dismissEdit,
                        onSubmit = viewModel::submitEdit,
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarPlaceholder() {
    val palette = LocalPalette.current
    Box(Modifier.fillMaxSize().padding(horizontal = 24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "CALENDAR",
                style = appStyle(28.sp, FontWeight.Normal, letterSpacing = 0.08.em),
                color = palette.text,
            )
            Text(
                "Week, day, and month views come next.",
                modifier = Modifier.padding(top = 8.dp),
                style = appStyle(13.sp, FontWeight.Medium),
                color = palette.textMuted,
            )
        }
    }
}
