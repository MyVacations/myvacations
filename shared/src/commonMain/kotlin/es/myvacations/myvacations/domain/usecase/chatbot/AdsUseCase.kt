package es.myvacations.myvacations.domain.usecase.chatbot

import es.myvacations.myvacations.domain.repository.AdsController

class AdsUseCase(
    val adscontroller: AdsController
) {
    fun showInterstitial() = adscontroller.showInterstitial()
}