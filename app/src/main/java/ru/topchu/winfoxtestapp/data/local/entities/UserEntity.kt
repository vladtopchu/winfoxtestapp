package ru.topchu.winfoxtestapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    val userId: String,
    val email: String,
    val firstname: String? = null,
    val lastname: String? = null,
    val middlename: String? = null,
    val birthdate: String? = null,
    val birth_place: String? = null,
    val organization: String? = null,
    val position: String? = null,
    val preferences: List<String> = emptyList(),
    @PrimaryKey(autoGenerate = true) val id: Long = 0L
)