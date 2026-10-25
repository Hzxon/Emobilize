package ayodong.emobilize.core.ui.dock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ayodong.emobilize.core.ui.theme.AppMetrics
import ayodong.emobilize.core.ui.theme.LocalPalette
import ayodong.emobilize.core.ui.theme.appStyle

@Composable
fun BottomDock(
    tab: NavTab,
    onTab: (NavTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalPalette.current
    Column(modifier.fillMaxWidth().background(palette.card)) {
        Box(Modifier.fillMaxWidth().height(2.dp).background(palette.text))
        Row(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(AppMetrics.navHeight - 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DockTab(
                label = "Departures",
                selected = tab == NavTab.Tasks,
                icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(22.dp)) },
                onClick = { onTab(NavTab.Tasks) },
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(AppMetrics.fabSize))
            DockTab(
                label = "Route map",
                selected = tab == NavTab.Calendar,
                icon = { Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier.size(22.dp)) },
                onClick = { onTab(NavTab.Calendar) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// A flat tab with a thick ink bar on top while selected, like the active platform on a sign.
@Composable
private fun DockTab(
    label: String,
    selected: Boolean,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    val palette = LocalPalette.current
    val tint = if (selected) palette.text else palette.textMuted
    Box(
        modifier
            .fillMaxHeight()
            .selectable(selected = selected, onClick = onClick, role = Role.Tab),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(if (selected) palette.text else Color.Transparent),
        )
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        ) {
            CompositionLocalProvider(LocalContentColor provides tint) { icon() }
            Text(
                text = label.uppercase(),
                style = appStyle(12.sp, if (selected) FontWeight.ExtraBold else FontWeight.Bold, letterSpacing = 1.sp),
                color = tint,
                maxLines = 1,
            )
        }
    }
}

// The centre key: a solid square that fills the dock height, with a signal-yellow plus.
@Composable
internal fun FabFace(progress: Float, showCancel: Boolean) {
    val palette = LocalPalette.current
    Box(
        Modifier
            .fillMaxSize()
            .background(palette.primary),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            if (showCancel) Icons.Filled.Close else Icons.Filled.Add,
            contentDescription = null,
            tint = palette.onFilled,
            modifier = Modifier
                .size(30.dp)
                .rotate(progress * 90f),
        )
    }
}
