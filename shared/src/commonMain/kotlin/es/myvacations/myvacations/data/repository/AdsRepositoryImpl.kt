package es.myvacations.myvacations.data.repository

import es.myvacations.myvacations.domain.repository.AdsController

expect class AdsRepositoryImpl() : AdsController {
    override fun showInterstitial()
}