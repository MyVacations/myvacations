package es.myvacations.myvacations.data.repository

import es.myvacations.myvacations.domain.repository.FirebaseAuthRepository
import es.myvacations.myvacations.presentation.dashboard.UserInfo
import kotlinx.coroutines.flow.Flow

expect class FirebaseAuthImpl(
) : FirebaseAuthRepository {
    override fun getUserInfo(): Flow<UserInfo>
    override fun signInWithAnonymously()
    override fun loginShouldShow(): Boolean
    override suspend fun signUpWithEmaiAndPassword(email: String, password: String): Result<Unit>
    override suspend fun signInWithEmaiAndPassword(email: String, password: String): Result<Unit>
    override suspend fun signInWithGoogle(): Result<Unit>
    override suspend fun resetPassword(email: String): Result<Unit>
    override fun signOut()
}