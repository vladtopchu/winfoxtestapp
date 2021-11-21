package ru.topchu.winfoxtestapp.data.remote.dto

data class RegistrationDto(
    val email: String,
    val firstname: String,
    val lastname: String,
    val password: String,
    val id: String?
)
