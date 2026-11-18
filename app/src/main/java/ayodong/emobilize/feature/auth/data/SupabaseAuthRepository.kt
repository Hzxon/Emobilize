package ayodong.emobilize.feature.auth.data

import ayodong.emobilize.feature.auth.domain.Account
import ayodong.emobilize.feature.auth.domain.AuthError
import ayodong.emobilize.feature.auth.domain.AuthOutcome
import ayodong.emobilize.feature.auth.domain.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.exceptions.HttpRequestException
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

// Supabase Auth with email and password. The session is saved by supabase-kt, so the user
// stays signed in across launches. A null client means the app was built without Supabase keys.
class SupabaseAuthRepository(private val client: SupabaseClient?) : AuthRepository {

    override suspend fun register(name: String, email: String, password: String): AuthOutcome = call { auth ->
        auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject { put("name", name) }
        }
        // With "Confirm email" on, sign-up creates the account but no session.
        if (auth.currentUserOrNull() == null) {
            AuthOutcome.Failure(AuthError.ConfirmEmail)
        } else {
            AuthOutcome.Success(Account(name, email))
        }
    }

    override suspend fun login(email: String, password: String): AuthOutcome = call { auth ->
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        AuthOutcome.Success(Account(name = "", email = email))
    }

    private suspend fun call(block: suspend (Auth) -> AuthOutcome): AuthOutcome {
        val auth = client?.auth ?: return AuthOutcome.Failure(AuthError.Offline)
        return try {
            block(auth)
        } catch (e: AuthRestException) {
            AuthOutcome.Failure(
                when (e.errorCode) {
                    AuthErrorCode.UserAlreadyExists, AuthErrorCode.EmailExists -> AuthError.EmailTaken
                    AuthErrorCode.EmailNotConfirmed -> AuthError.ConfirmEmail
                    AuthErrorCode.WeakPassword -> AuthError.ShortPassword
                    else -> AuthError.WrongCredentials
                },
            )
        } catch (_: HttpRequestException) {
            AuthOutcome.Failure(AuthError.Offline)
        }
    }
}
