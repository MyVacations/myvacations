package es.myvacations.myvacations.domain.repository

import es.myvacations.myvacations.domain.model.UserDomain
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(): Flow<UserDomain?>
    suspend fun saveUser(user: UserDomain)
    suspend fun updateUser(user: UserDomain)
}