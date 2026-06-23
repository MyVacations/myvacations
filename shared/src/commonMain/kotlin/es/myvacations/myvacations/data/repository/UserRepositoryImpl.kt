package es.myvacations.myvacations.data.repository

import es.myvacations.myvacations.data.datasource.UserLocalDataSource
import es.myvacations.myvacations.domain.mapper.toDomainModel
import es.myvacations.myvacations.domain.model.UserDomain
import es.myvacations.myvacations.domain.repository.UserRepository
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val localDataSource: UserLocalDataSource
) : UserRepository {

    override fun getUser() = localDataSource.getUser().map { it?.toDomainModel() }

    override suspend fun saveUser(user: UserDomain) {
        localDataSource.insertUser(user.id, user.name ?: "")
    }

    override suspend fun updateUser(user: UserDomain) {
        localDataSource.updateUser(user.id, user.name ?: "")
    }
}