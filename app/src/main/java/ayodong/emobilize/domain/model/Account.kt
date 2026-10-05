package ayodong.emobilize.domain.model

data class Account(
    val name: String,
    val email: String,
)

enum class AuthError {
    InvalidName,
    InvalidEmail,
    ShortPassword,
    EmailTaken,
    WrongCredentials,
}

sealed interface AuthOutcome {
    data class Success(val account: Account) : AuthOutcome
    data class Failure(val error: AuthError) : AuthOutcome
}
