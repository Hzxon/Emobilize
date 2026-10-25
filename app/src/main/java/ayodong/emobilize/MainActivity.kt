package ayodong.emobilize

import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import ayodong.emobilize.core.di.LocalAppContainer
import ayodong.emobilize.core.model.ThemeMode
import ayodong.emobilize.core.navigation.AppNavHost
import ayodong.emobilize.core.navigation.ShellViewModel

class MainActivity : ComponentActivity() {
    private val container by lazy { (application as EmobilizeApplication).container }

    override fun onCreate(savedInstanceState: Bundle?) {
        val dark = savedThemeIsDark()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            splashScreen.setSplashScreenTheme(
                if (dark) R.style.Theme_Emobilize_SplashDark else R.style.Theme_Emobilize_SplashLight,
            )
        }
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(
            ColorDrawable(getColor(if (dark) R.color.shadcn_background_dark else R.color.shadcn_background)),
        )
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalAppContainer provides container) {
                AppNavHost(shell = viewModel(factory = ShellViewModel.factory(container)))
            }
        }
    }

    private fun savedThemeIsDark(): Boolean {
        val mode = container.appSettings.settings.value.themeMode
        return when (mode) {
            ThemeMode.Dark -> true
            ThemeMode.Light -> false
            ThemeMode.System ->
                (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                    Configuration.UI_MODE_NIGHT_YES
        }
    }
}
