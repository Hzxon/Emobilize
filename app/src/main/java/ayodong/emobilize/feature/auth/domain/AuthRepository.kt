package ayodong.emobilize.feature.auth.domain


interface AuthRepository {
    fun register(name: String, email: String, password: String): AuthOutcome
    fun login(email: String, password: String): AuthOutcome
}
