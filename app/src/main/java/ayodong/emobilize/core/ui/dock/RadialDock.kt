package ayodong.emobilize.core.ui.dock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ayodong.emobilize.core.model.DateRange
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
    Column(modifier.fillMaxWidth().background(palette.navBg)) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(palette.scheme.border))
        Row(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(AppMetrics.navHeight - 1.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
        DockTab(
            label = "Tasks",
            selected = tab == NavTab.Tasks,
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(22.dp)) },
            onClick = { onTab(NavTab.Tasks) },
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(AppMetrics.fabSize + 8.dp))
        DockTab(
            label = "Calendar",
            selected = tab == NavTab.Calendar,
            icon = { Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier.size(22.dp)) },
            onClick = { onTab(NavTab.Calendar) },
            modifier = Modifier.weight(1f),
        )
        }
    }
}
@Composable
private fun DockTab(
    label: String,
    selected: Boolean,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    val palette = LocalPalette.current
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(Modifier.graphicsLayer { alpha = 1f }, contentAlignment = Alignment.Center) {
            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.material3.LocalContentColor provides if (selected) palette.text else palette.textMuted,
            ) {
                icon()
            }
        }
        Text(
            text = label,
            style = appStyle(12.sp, if (selected) FontWeight.SemiBold else FontWeight.Medium),
            color = if (selected) palette.text else palette.textMuted,
        )
        Box(
            Modifier
                .padding(top = 2.dp)
                .size(width = 20.dp, height = 3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (selected) palette.primary else Color.Transparent),
        )
    }
}

@Composable
internal fun FabFace(progress: Float, showCancel: Boolean) {
    val palette = LocalPalette.current
    val lift = progress.coerceIn(0f, 1f)
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(72.dp)
                .shadow((8f * lift).dp, CircleShape)
                .clip(CircleShape)
                .background(palette.primary)
                .border(2.dp, palette.surface, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                if (showCancel) Icons.Filled.Close else Icons.Filled.Add,
                contentDescription = null,
                tint = palette.onFilled,
                modifier = Modifier
                    .size(22.dp)
                    .rotate(progress * 90f),
            )
        }
    }
}
