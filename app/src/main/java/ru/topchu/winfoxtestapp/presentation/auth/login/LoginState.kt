package ru.topchu.winfoxtestapp.presentation.auth.login

import ru.topchu.winfoxtestapp.data.remote.dto.LoginDto

data class LoginState (
    val response: LoginDto? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)