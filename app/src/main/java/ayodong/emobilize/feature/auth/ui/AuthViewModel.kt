package ayodong.emobilize.feature.auth.ui

import androidx.lifecycle.ViewModel
import ayodong.emobilize.feature.auth.domain.AuthError
import ayodong.emobilize.feature.auth.domain.AuthOutcome
import ayodong.emobilize.feature.auth.domain.LoginUseCase
import ayodong.emobilize.feature.auth.domain.RegisterAccountUseCase
import ayodong.emobilize.feature.auth.domain.isEmail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class AuthPage { Login, Register }

data class AuthUiState(
    val page: AuthPage = AuthPage.Login,
    val signedIn: Boolean = false,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirm: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmError: String? = null,
)

class AuthViewModel(
    private val login: LoginUseCase,
    private val register: RegisterAccountUseCase,
) : ViewModel() {
    private val state = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = state.asStateFlow()

    fun showLogin() = showPage(AuthPage.Login)

    fun showRegister() = showPage(AuthPage.Register)

    fun updateName(value: String) = state.update { it.copy(name = value, nameError = null) }

    fun updateEmail(value: String) = state.update { it.copy(email = value, emailError = null) }

    fun updatePassword(value: String) = state.update { it.copy(password = value, passwordError = null) }

    fun updateConfirm(value: String) = state.update { it.copy(confirm = value, confirmError = null) }

    fun submit() {
        if (state.value.page == AuthPage.Login) submitLogin() else submitRegister()
    }

    fun submitLogin() {
        val current = state.value
        val checked = current.copy(
            emailError = emailProblem(current.email),
            passwordError = if (current.password.isEmpty()) "Enter your password" else null,
        )
        if (checked.emailError != null || checked.passwordError != null) {
            state.value = checked
            return
        }
        state.value = when (val outcome = login(current.email, current.password)) {
            is AuthOutcome.Success -> checked.copy(signedIn = true)
            is AuthOutcome.Failure -> checked.copy(passwordError = message(outcome.error))
        }
    }

    fun submitRegister() {
        val current = state.value
        val checked = current.copy(
            nameError = if (current.name.isBlank()) "Enter your name" else null,
            emailError = emailProblem(current.email),
            passwordError = when {
                current.password.isEmpty() -> "Enter your password"
                current.password.length < 6 -> "Use at least 6 characters"
                else -> null
            },
            confirmError = when {
                current.confirm.isEmpty() -> "Confirm your password"
                current.confirm != current.password -> "Passwords do not match"
                else -> null
            },
        )
        if (checked.nameError != null || checked.emailError != null || checked.passwordError != null || checked.confirmError != null) {
            state.value = checked
            return
        }
        state.value = when (val outcome = register(current.name, current.email, current.password)) {
            is AuthOutcome.Success -> checked.copy(signedIn = true)
            is AuthOutcome.Failure -> when (outcome.error) {
                AuthError.EmailTaken, AuthError.InvalidEmail -> checked.copy(emailError = message(outcome.error))
                AuthError.InvalidName -> checked.copy(nameError = message(outcome.error))
                AuthError.ShortPassword, AuthError.WrongCredentials -> checked.copy(passwordError = message(outcome.error))
            }
        }
    }

    private fun showPage(page: AuthPage) = state.update {
        it.copy(
            page = page,
            password = "",
            confirm = "",
            nameError = null,
            emailError = null,
            passwordError = null,
            confirmError = null,
        )
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
