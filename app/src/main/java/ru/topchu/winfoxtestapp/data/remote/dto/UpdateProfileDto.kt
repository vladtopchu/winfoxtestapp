package ru.topchu.winfoxtestapp.data.remote.dto

data class UpdateProfileDto(
    val email: String,
    val firstname: String,
    val lastname: String,
    val middlename: String,
    val birthdate: String,
    val birth_place: String,
    val organization: String,
    val position: String,
    val preferences: List<String>,
    val id: String?
)
