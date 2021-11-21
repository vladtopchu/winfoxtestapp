package ru.topchu.winfoxtestapp.presentation.auth.registration

import ru.topchu.winfoxtestapp.data.remote.dto.RegistrationDto

data class RegistrationState (
    val response: RegistrationDto? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)