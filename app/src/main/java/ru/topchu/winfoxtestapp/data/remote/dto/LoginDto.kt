package ru.topchu.winfoxtestapp.data.remote.dto

import ru.topchu.winfoxtestapp.data.local.entities.UserEntity

data class LoginDto(
    val email: String,
    val password: String,
    val id: String? = null
) {
    fun toUserEntity() = UserEntity(
        userId = id!!,
        email = email
    )
}
