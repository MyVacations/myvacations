package es.myvacations.myvacations.data.repository

import es.myvacations.myvacations.domain.repository.FirebaseAuthRepository
import es.myvacations.myvacations.presentation.dashboard.UserInfo
import kotlinx.coroutines.flow.Flow

actual class FirebaseAuthImpl actual constructor() :
    FirebaseAuthRepository {
    actual override fun getUserInfo(): Flow<UserInfo> {
        TODO("Not yet implemented")
    }

    actual override fun signInWithAnonymously() {
    }

    actual override fun loginShouldShow(): Boolean {
        TODO("Not yet implemented")
    }

    actual override suspend fun signUpWithEmaiAndPassword(email: String, password: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    actual override suspend fun signInWithEmaiAndPassword(email: String, password: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    actual override suspend fun signInWithGoogle(): Result<Unit> {
        TODO("Not yet implemented")
    }

    actual override suspend fun resetPassword(email: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    actual override fun signOut() {
        TODO("Not yet implemented")
    }
}