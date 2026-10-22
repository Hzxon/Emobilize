package ayodong.emobilize

import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import ayodong.emobilize.data.repository.PreferencesAppSettingsRepository
import ayodong.emobilize.di.AppContainer
import ayodong.emobilize.di.AuthViewModelFactory
import ayodong.emobilize.di.TasksViewModelFactory
import ayodong.emobilize.domain.model.ThemeMode
import ayodong.emobilize.ui.EmobilizeApp

class MainActivity : ComponentActivity() {
    private val container by lazy { AppContainer(applicationContext) }

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
            EmobilizeApp(
                viewModel = viewModel(factory = TasksViewModelFactory(container)),
                authViewModel = viewModel(factory = AuthViewModelFactory(container)),
            )
        }
    }

    private fun savedThemeIsDark(): Boolean {
        val mode = PreferencesAppSettingsRepository(this).get().themeMode
        return when (mode) {
            ThemeMode.Dark -> true
            ThemeMode.Light -> false
            ThemeMode.System ->
                (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                    Configuration.UI_MODE_NIGHT_YES
        }
    }
}
