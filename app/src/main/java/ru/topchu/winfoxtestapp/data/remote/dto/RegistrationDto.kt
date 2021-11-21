package ru.topchu.winfoxtestapp.data.remote.dto

import ru.topchu.winfoxtestapp.data.local.entities.UserEntity

data class RegistrationDto(
    val email: String,
    val firstname: String,
    val lastname: String,
    val password: String,
    val id: String?
) {
    fun toUserEntity() = UserEntity(
        userId = id!!,
        email = email,
        firstname = firstname,
        lastname = lastname
    )
}
