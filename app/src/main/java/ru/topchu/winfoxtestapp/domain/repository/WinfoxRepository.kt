package ru.topchu.winfoxtestapp.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.topchu.winfoxtestapp.data.remote.dto.LoginDto
import ru.topchu.winfoxtestapp.data.remote.dto.RegistrationDto
import ru.topchu.winfoxtestapp.data.remote.dto.UpdateProfileDto
import ru.topchu.winfoxtestapp.utils.Resource

interface WinfoxRepository {
    fun registerUser(registrationForm: RegistrationDto): Flow<Resource<RegistrationDto>>
    fun checkLogin(loginForm: LoginDto): Flow<Resource<LoginDto>>
    fun updateProfile(updateProfileForm: UpdateProfileDto): Flow<Resource<UpdateProfileDto>>
}