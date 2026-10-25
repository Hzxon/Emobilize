package ayodong.emobilize.core.ui.dock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import ayodong.emobilize.core.ui.theme.LocalPalette

// The floating key: a solid square. At rest it shows the screen a tap goes to. As the dial
// opens it turns 45 degrees into a diamond hub with an upright close mark.
@Composable
internal fun FabFace(progress: Float, open: Boolean, tab: NavTab) {
    val palette = LocalPalette.current
    val turn = 45f * progress.coerceIn(0f, 1f)
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(64.dp - 10.dp * progress.coerceIn(0f, 1f))
                .rotate(turn)
                .background(palette.signal)
                .border(2.dp, palette.signalInk),
        )
        Icon(
            when {
                open -> Icons.Filled.Close
                tab == NavTab.Tasks -> Icons.Filled.DateRange
                else -> Icons.AutoMirrored.Filled.List
            },
            contentDescription = null,
            tint = palette.signalInk,
            modifier = Modifier.size(26.dp),
        )
    }
}
