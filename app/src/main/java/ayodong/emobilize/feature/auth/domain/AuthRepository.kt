package ayodong.emobilize.feature.auth.domain


interface AuthRepository {
    suspend fun register(name: String, email: String, password: String): AuthOutcome
    suspend fun login(email: String, password: String): AuthOutcome
}
