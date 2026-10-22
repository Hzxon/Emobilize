package ayodong.emobilize.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ayodong.emobilize.domain.model.AuthError
import ayodong.emobilize.domain.model.AuthOutcome
import ayodong.emobilize.domain.usecase.LoginUseCase
import ayodong.emobilize.domain.usecase.RegisterAccountUseCase
import ayodong.emobilize.domain.usecase.isEmail

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
    var nameError by mutableStateOf<String?>(null)
        private set
    var emailError by mutableStateOf<String?>(null)
        private set
    var passwordError by mutableStateOf<String?>(null)
        private set
    var confirmError by mutableStateOf<String?>(null)
        private set

    fun showLogin() {
        page = AuthPage.Login
        password = ""
        confirm = ""
        clearErrors()
    }

    fun showRegister() {
        page = AuthPage.Register
        password = ""
        confirm = ""
        clearErrors()
    }

    fun updateName(value: String) {
        name = value
        nameError = null
    }

    fun updateEmail(value: String) {
        email = value
        emailError = null
    }

    fun updatePassword(value: String) {
        password = value
        passwordError = null
    }

    fun updateConfirm(value: String) {
        confirm = value
        confirmError = null
    }

    fun submitLogin() {
        emailError = emailProblem(email)
        passwordError = if (password.isEmpty()) "Enter your password" else null
        if (emailError != null || passwordError != null) return
        when (val outcome = login(email, password)) {
            is AuthOutcome.Success -> signedIn = true
            is AuthOutcome.Failure -> passwordError = message(outcome.error)
        }
    }

    fun submitRegister() {
        nameError = if (name.isBlank()) "Enter your name" else null
        emailError = emailProblem(email)
        passwordError = when {
            password.isEmpty() -> "Enter your password"
            password.length < 6 -> "Use at least 6 characters"
            else -> null
        }
        confirmError = when {
            confirm.isEmpty() -> "Confirm your password"
            confirm != password -> "Passwords do not match"
            else -> null
        }
        if (nameError != null || emailError != null || passwordError != null || confirmError != null) return
        when (val outcome = register(name, email, password)) {
            is AuthOutcome.Success -> signedIn = true
            is AuthOutcome.Failure -> when (outcome.error) {
                AuthError.EmailTaken -> emailError = message(outcome.error)
                AuthError.InvalidName -> nameError = message(outcome.error)
                AuthError.InvalidEmail -> emailError = message(outcome.error)
                AuthError.ShortPassword -> passwordError = message(outcome.error)
                AuthError.WrongCredentials -> passwordError = message(outcome.error)
            }
        }
    }

    private fun clearErrors() {
        nameError = null
        emailError = null
        passwordError = null
        confirmError = null
    }

    private fun emailProblem(value: String): String? {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return "Enter your email"
        if (!isEmail(trimmed)) return "Enter a valid email"
        return null
    }

    private fun message(error: AuthError): String = when (error) {
        AuthError.InvalidName -> "Enter your name"
        AuthError.InvalidEmail -> "Enter a valid email"
        AuthError.ShortPassword -> "Use at least 6 characters"
        AuthError.EmailTaken -> "That email is already registered"
        AuthError.WrongCredentials -> "Email or password is incorrect"
    }
}
