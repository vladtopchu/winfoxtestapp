package ru.topchu.winfoxtestapp.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
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

    @GET("/getImage")
    suspend fun getImage(@Query("filename") filename: String): ImageDto

    companion object {
        const val BASE_URL = "http://94.127.67.113:8099/"
    }
}