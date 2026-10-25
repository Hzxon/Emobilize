package ayodong.emobilize.feature.splash.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ayodong.emobilize.R
import ayodong.emobilize.core.ui.theme.LocalPalette
import ayodong.emobilize.core.ui.theme.appStyle
import kotlinx.coroutines.delay

private val WordOnLight = Color(0xFF262626)
private val WordOnDark = Color(0xFFE8E8E8)

@Composable
fun SplashScreen(
    isDark: Boolean,
    onFinished: () -> Unit,
) {
    val inspection = LocalInspectionMode.current
    var finished by remember { mutableStateOf(false) }
    val finish = {
        if (!finished) {
            finished = true
            onFinished()
        }
    }
    if (!inspection) {
        LaunchedEffect(Unit) {
            delay(1100)
            finish()
        }
    }
    val background = LocalPalette.current.surface
    val word = if (isDark) WordOnDark else WordOnLight
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .clickable(onClick = finish)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(
                if (isDark) R.drawable.ic_emobilize_mark_dark else R.drawable.ic_emobilize_mark_light,
            ),
            contentDescription = "Emobilize",
            modifier = Modifier.height(52.dp),
            contentScale = ContentScale.Fit,
        )
        Text(
            "MOBILIZE",
            style = appStyle(28.sp, FontWeight.Bold, letterSpacing = 0.5.sp),
            color = word,
        )
    }
}
