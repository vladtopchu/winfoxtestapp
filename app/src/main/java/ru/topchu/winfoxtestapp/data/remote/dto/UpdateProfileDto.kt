package ru.topchu.winfoxtestapp.data.remote.dto

import ru.topchu.winfoxtestapp.data.local.entities.UserEntity

data class UpdateProfileDto(
    var email: String? = null,
    var firstname: String? = null,
    var lastname: String? = null,
    var middlename: String? = null,
    var birthdate: String? = null,
    var birth_place: String? = null,
    var organization: String? = null,
    var position: String? = null,
    var preferences: List<String> = emptyList(),
    var id: String? = null
) {
    fun toEntity() = UserEntity(
        userId = id,
        email = email,
        firstname = firstname,
        lastname = lastname,
        middlename = middlename,
        birthdate = birthdate,
        birth_place = birth_place,
        organization = organization,
        position = position,
        preferences = preferences
    )
}
