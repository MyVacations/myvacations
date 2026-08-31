package es.myvacations.myvacations.domain.repository

import es.myvacations.myvacations.presentation.dashboard.UserInfo
import kotlinx.coroutines.flow.Flow

interface FirebaseAuthRepository {
    fun getUserInfo(): Flow<UserInfo>
    fun signInWithAnonymously()
    fun loginShouldShow(): Boolean
    suspend fun signUpWithEmaiAndPassword(email: String, password: String): Result<Unit>
    suspend fun signInWithEmaiAndPassword(email: String, password: String): Result<Unit>
    suspend fun signInWithGoogle(): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
    fun signOut()
}