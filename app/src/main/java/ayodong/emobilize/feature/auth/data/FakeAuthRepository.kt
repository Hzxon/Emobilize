package ayodong.emobilize.feature.auth.data

import ayodong.emobilize.feature.auth.domain.Account
import ayodong.emobilize.feature.auth.domain.AuthError
import ayodong.emobilize.feature.auth.domain.AuthOutcome
import ayodong.emobilize.feature.auth.domain.AuthRepository

// In-memory AuthRepository for previews and unit tests. Passwords are compared as plain text.
class FakeAuthRepository : AuthRepository {
    private val accounts = mutableMapOf<String, Pair<Account, String>>()

    override fun register(name: String, email: String, password: String): AuthOutcome {
        if (email in accounts) return AuthOutcome.Failure(AuthError.EmailTaken)
        val account = Account(name = name, email = email)
        accounts[email] = account to password
        return AuthOutcome.Success(account)
    }

    override fun login(email: String, password: String): AuthOutcome {
        val (account, stored) = accounts[email] ?: return AuthOutcome.Failure(AuthError.WrongCredentials)
        return if (stored == password) AuthOutcome.Success(account) else AuthOutcome.Failure(AuthError.WrongCredentials)
    }
}
