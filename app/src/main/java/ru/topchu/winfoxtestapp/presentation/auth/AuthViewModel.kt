package ru.topchu.winfoxtestapp.presentation.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.topchu.winfoxtestapp.data.local.AppDatabase
import ru.topchu.winfoxtestapp.data.local.entities.UserEntity
import ru.topchu.winfoxtestapp.data.remote.dto.LoginDto
import ru.topchu.winfoxtestapp.data.remote.dto.RegistrationDto
import ru.topchu.winfoxtestapp.domain.repository.WinfoxRepository
import ru.topchu.winfoxtestapp.utils.Resource
import ru.topchu.winfoxtestapp.utils.SharedPref
import ru.topchu.winfoxtestapp.utils.asLiveData
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class AuthViewModel @Inject constructor (
    private val sharedPref: SharedPref,
    private val database: AppDatabase
): ViewModel() {

    private val _status = MutableLiveData(false)
    val status = _status.asLiveData()

    fun wipeStatus() {
        _status.postValue(false)
    }

    fun proceedAuth(loginData: LoginDto) {
        viewModelScope.launch {
            sharedPref.setUserId(loginData.id!!)
            database.userDao().createUser(loginData.toUserEntity())
            _status.postValue(true)
        }
    }

    fun proceedAuth(registrationData: RegistrationDto) {
        viewModelScope.launch {
            sharedPref.setUserId(registrationData.id!!)
            database.userDao().createUser(registrationData.toUserEntity())
            _status.postValue(true)
        }
    }
}