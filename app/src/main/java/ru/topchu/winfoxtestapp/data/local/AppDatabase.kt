package ru.topchu.winfoxtestapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.topchu.winfoxtestapp.data.local.daos.UserDao
import ru.topchu.winfoxtestapp.data.local.entities.UserEntity

@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}