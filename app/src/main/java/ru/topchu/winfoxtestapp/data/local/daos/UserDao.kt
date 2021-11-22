package ru.topchu.winfoxtestapp.data.local.daos

import androidx.room.*
import ru.topchu.winfoxtestapp.data.local.entities.UserEntity

@Dao
interface UserDao {

    @Insert
    suspend fun createUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE userId = :id")
    suspend fun getUserById(id: String): UserEntity

    @Update(entity = UserEntity::class)
    suspend fun updateUser(user: UserEntity): Int

    @Query("DELETE FROM users WHERE userId = :id")
    suspend fun deleteUser(id: String)
}