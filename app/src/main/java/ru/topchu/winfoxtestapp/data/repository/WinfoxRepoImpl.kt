package ru.topchu.winfoxtestapp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Response
import ru.topchu.winfoxtestapp.data.remote.WinfoxApi
import ru.topchu.winfoxtestapp.data.remote.dto.LoginDto
import ru.topchu.winfoxtestapp.data.remote.dto.RegistrationDto
import ru.topchu.winfoxtestapp.data.remote.dto.UpdateProfileDto
import ru.topchu.winfoxtestapp.domain.repository.WinfoxRepository
import ru.topchu.winfoxtestapp.utils.Resource
import ru.topchu.winfoxtestapp.utils.SimpleResource
import timber.log.Timber
import java.io.IOException

class WinfoxRepoImpl(
    private val api: WinfoxApi
): WinfoxRepository {

    override fun registerUser(registrationForm: RegistrationDto): Flow<Resource<RegistrationDto>> = flow {
        emit(Resource.Loading())
        try {
            val registrationResponse = api.registerUser(registrationForm)
            emit(Resource.Success(registrationResponse))
        } catch (e: HttpException) {
            Timber.d(e.toString())
            emit(Resource.Error(
                message = "Сервер ответил ошибкой"
            ))
        } catch (e: IOException) {
            Timber.d(e.toString())
            emit(Resource.Error(
                message = "Проверьте интернет-соединение"
            ))
        }
    }

    override fun checkLogin(loginForm: LoginDto): Flow<Resource<LoginDto>>  = flow {
        emit(Resource.Loading())
        try {
            val loginResponse = api.checkLogin(loginForm)
            emit(Resource.Success(loginResponse))
        } catch (e: HttpException) {
            Timber.d(e.toString())
            emit(Resource.Error(
                message = "Сервер ответил ошибкой"
            ))
        } catch (e: IOException) {
            Timber.d(e.toString())
            emit(Resource.Error(
                message = "Проверьте интернет-соединение"
            ))
        }
    }

    override fun updateProfile(updateProfileForm: UpdateProfileDto): Flow<Resource<UpdateProfileDto>>  = flow {
        emit(Resource.Loading())
        try {
            val updateResponse = api.updateProfile(updateProfileForm)
            emit(Resource.Success(updateResponse))
        } catch (e: HttpException) {
            Timber.d(e.toString())
            emit(Resource.Error(
                message = "Сервер ответил ошибкой"
            ))
        } catch (e: IOException) {
            Timber.d(e.toString())
            emit(Resource.Error(
                message = "Проверьте интернет-соединение"
            ))
        }
    }
}