package ayodong.emobilize.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ayodong.emobilize.domain.model.AuthError
import ayodong.emobilize.domain.model.AuthOutcome
import ayodong.emobilize.domain.usecase.LoginUseCase
import ayodong.emobilize.domain.usecase.RegisterAccountUseCase

enum class AuthPage { Login, Register }

class AuthViewModel(
    private val login: LoginUseCase,
    private val register: RegisterAccountUseCase,
) : ViewModel() {
    var page by mutableStateOf(AuthPage.Login)
        private set
    var signedIn by mutableStateOf(false)
        private set
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirm by mutableStateOf("")
    var error by mutableStateOf<String?>(null)
        private set

    fun showLogin() {
        page = AuthPage.Login
        password = ""
        confirm = ""
        error = null
    }

    fun showRegister() {
        page = AuthPage.Register
        password = ""
        confirm = ""
        error = null
    }

    fun submitLogin() {
        when (val outcome = login(email, password)) {
            is AuthOutcome.Success -> signedIn = true
            is AuthOutcome.Failure -> error = message(outcome.error)
        }
    }

    fun submitRegister() {
        if (password != confirm) {
            error = "Passwords do not match"
            return
        }
        when (val outcome = register(name, email, password)) {
            is AuthOutcome.Success -> signedIn = true
            is AuthOutcome.Failure -> error = message(outcome.error)
        }
    }

    fun clearError() {
        error = null
    }

    private fun message(error: AuthError): String = when (error) {
        AuthError.InvalidName -> "Enter your name"
        AuthError.InvalidEmail -> "Enter a valid email"
        AuthError.ShortPassword -> "Use at least 6 characters"
        AuthError.EmailTaken -> "That email is already registered"
        AuthError.WrongCredentials -> "Email or password is incorrect"
    }
}
