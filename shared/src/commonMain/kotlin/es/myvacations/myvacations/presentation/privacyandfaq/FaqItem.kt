package es.myvacations.myvacations.presentation.privacyandfaq

import org.jetbrains.compose.resources.StringResource

data class FaqItem(
    val question: StringResource,
    val answer: StringResource
)