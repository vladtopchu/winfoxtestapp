package ru.topchu.winfoxtestapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.topchu.winfoxtestapp.data.remote.dto.UpdateProfileDto

@Entity(tableName = "users")
data class UserEntity(
    val userId: String? = null,
    val email: String? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val middlename: String? = null,
    val birthdate: String? = null,
    val birth_place: String? = null,
    val organization: String? = null,
    val position: String? = null,
    val preferences: List<String> = emptyList(),
    @PrimaryKey(autoGenerate = true) var id: Long = 0L
) {
    fun toDto() = UpdateProfileDto(
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