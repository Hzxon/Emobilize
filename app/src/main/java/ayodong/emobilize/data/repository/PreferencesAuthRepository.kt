package ayodong.emobilize.data.repository

import android.content.Context
import ayodong.emobilize.domain.model.Account
import ayodong.emobilize.domain.model.AuthError
import ayodong.emobilize.domain.model.AuthOutcome
import ayodong.emobilize.domain.repository.AuthRepository
import java.security.MessageDigest

class PreferencesAuthRepository(context: Context) : AuthRepository {
    private val prefs = context.applicationContext.getSharedPreferences(PrefsName, Context.MODE_PRIVATE)
    private val accounts = load().toMutableMap()

    override fun register(name: String, email: String, password: String): AuthOutcome {
        if (accounts.containsKey(email)) return AuthOutcome.Failure(AuthError.EmailTaken)
        val account = Account(name = name, email = email)
        accounts[email] = StoredAccount(account, hash(password))
        save()
        return AuthOutcome.Success(account)
    }

    override fun login(email: String, password: String): AuthOutcome {
        val stored = accounts[email] ?: return AuthOutcome.Failure(AuthError.WrongCredentials)
        if (stored.hash != hash(password)) return AuthOutcome.Failure(AuthError.WrongCredentials)
        return AuthOutcome.Success(stored.account)
    }

    private fun load(): Map<String, StoredAccount> {
        val raw = prefs.getString(AccountsKey, null).orEmpty()
        if (raw.isEmpty()) return emptyMap()
        return raw.split(Record).mapNotNull { line ->
            val parts = line.split(Field)
            if (parts.size != 3) return@mapNotNull null
            val account = Account(name = parts[1], email = parts[0])
            account.email to StoredAccount(account, parts[2])
        }.toMap()
    }

    private fun save() {
        val raw = accounts.values.joinToString(Record) {
            listOf(it.account.email, it.account.name, it.hash).joinToString(Field)
        }
        prefs.edit().putString(AccountsKey, raw).apply()
    }

    private fun hash(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    private data class StoredAccount(val account: Account, val hash: String)

    private companion object {
        const val PrefsName = "emobilize_auth"
        const val AccountsKey = "accounts"
        const val Record = "\u001E"
        const val Field = "\u001F"
    }
}
