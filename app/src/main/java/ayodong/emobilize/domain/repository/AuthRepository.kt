package ayodong.emobilize.domain.repository

import ayodong.emobilize.domain.model.AuthOutcome

interface AuthRepository {
    fun register(name: String, email: String, password: String): AuthOutcome
    fun login(email: String, password: String): AuthOutcome
}
