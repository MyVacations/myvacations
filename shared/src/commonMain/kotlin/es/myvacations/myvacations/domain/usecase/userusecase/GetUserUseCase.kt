package es.myvacations.myvacations.domain.usecase.userusecase

import es.myvacations.myvacations.domain.repository.UserRepository

class GetUserUseCase(private val repository: UserRepository) {
    operator fun invoke() = repository.getUser()
}