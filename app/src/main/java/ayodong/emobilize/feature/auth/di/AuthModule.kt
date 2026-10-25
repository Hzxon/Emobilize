package ayodong.emobilize.feature.auth.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ayodong.emobilize.core.di.AppContainer
import ayodong.emobilize.feature.auth.data.PreferencesAuthRepository
import ayodong.emobilize.feature.auth.domain.LoginUseCase
import ayodong.emobilize.feature.auth.domain.RegisterAccountUseCase
import ayodong.emobilize.feature.auth.ui.AuthViewModel

// Auth data is read by no other feature, so its repository is built here instead of in AppContainer.
object AuthModule {
    fun authViewModel(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
        initializer {
            val repository = PreferencesAuthRepository(container.appContext)
            AuthViewModel(login = LoginUseCase(repository), register = RegisterAccountUseCase(repository))
        }
    }
}
