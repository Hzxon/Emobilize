package ayodong.emobilize.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ayodong.emobilize.R
import ayodong.emobilize.ui.theme.LocalPalette
import ayodong.emobilize.ui.theme.appStyle
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val palette = LocalPalette.current
    val alpha = remember { Animatable(0f) }
    val scrim = remember { MutableInteractionSource() }
    LaunchedEffect(Unit) {
        alpha.animateTo(1f, tween(420))
        delay(900)
        onFinished()
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(palette.surface)
            .clickable(interactionSource = scrim, indication = null, onClick = onFinished),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer { this.alpha = alpha.value },
        ) {
            Box(
                Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(palette.scheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_emobilize_logo),
                    contentDescription = "Emobilize Logo",
                    tint = palette.scheme.primaryForeground,
                    modifier = Modifier.size(42.dp),
                )
            }
            Spacer(Modifier.height(18.dp))
            Text(
                "Emobilize",
                style = appStyle(30.sp, FontWeight.Bold, lineHeight = 36.sp),
                color = palette.text,
            )
        }
    }
}
