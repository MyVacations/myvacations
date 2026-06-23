package es.myvacations.myvacations.domain.usecase.userusecase

import es.myvacations.myvacations.domain.model.UserDomain
import es.myvacations.myvacations.domain.repository.UserRepository

class UpdateUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(user: UserDomain) = repository.updateUser(user)
}