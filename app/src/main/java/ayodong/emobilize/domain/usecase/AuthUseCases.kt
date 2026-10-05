package ayodong.emobilize.domain.usecase

import ayodong.emobilize.domain.model.AuthError
import ayodong.emobilize.domain.model.AuthOutcome
import ayodong.emobilize.domain.repository.AuthRepository

class RegisterAccountUseCase(private val auth: AuthRepository) {
    operator fun invoke(name: String, email: String, password: String): AuthOutcome {
        val trimmedName = name.trim()
        val normalized = email.trim().lowercase()
        if (trimmedName.isEmpty()) return AuthOutcome.Failure(AuthError.InvalidName)
        if (!isEmail(normalized)) return AuthOutcome.Failure(AuthError.InvalidEmail)
        if (password.length < 6) return AuthOutcome.Failure(AuthError.ShortPassword)
        return auth.register(trimmedName, normalized, password)
    }
}

class LoginUseCase(private val auth: AuthRepository) {
    operator fun invoke(email: String, password: String): AuthOutcome {
        val normalized = email.trim().lowercase()
        if (!isEmail(normalized) || password.isEmpty()) {
            return AuthOutcome.Failure(AuthError.WrongCredentials)
        }
        return auth.login(normalized, password)
    }
}

private fun isEmail(value: String): Boolean {
    val at = value.indexOf('@')
    val dot = value.lastIndexOf('.')
    return at > 0 && dot > at + 1 && dot < value.lastIndex
}
