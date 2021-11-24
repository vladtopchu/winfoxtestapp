package ru.topchu.winfoxtestapp.data.remote

import okhttp3.MultipartBody
import retrofit2.http.*
import ru.topchu.winfoxtestapp.data.remote.dto.ImageDto
import ru.topchu.winfoxtestapp.data.remote.dto.LoginDto
import ru.topchu.winfoxtestapp.data.remote.dto.RegistrationDto
import ru.topchu.winfoxtestapp.data.remote.dto.UpdateProfileDto

interface WinfoxApi {

    @POST("/registerUser")
    suspend fun registerUser(@Body registrationForm: RegistrationDto): RegistrationDto

    @POST("/checkLogin")
    suspend fun checkLogin(@Body loginForm: LoginDto): LoginDto

    @POST("/updateProfile")
    suspend fun updateProfile(@Body updateProfile: UpdateProfileDto): UpdateProfileDto

    companion object {
        const val BASE_URL = "http://94.127.67.113:8099/"
    }
}