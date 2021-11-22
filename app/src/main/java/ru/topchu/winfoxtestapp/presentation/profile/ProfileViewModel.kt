package ru.topchu.winfoxtestapp.presentation.profile

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
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class ProfileViewModel @Inject constructor (
    private val sharedPref: SharedPref,
    private val database: AppDatabase
): ViewModel() {

    private val _userData = MutableLiveData<UserEntity?>(null)
    val userData = _userData.asLiveData()

    fun update() {
        viewModelScope.launch {
            if(sharedPref.getUserId() != null) {
                Timber.d(sharedPref.getUserId())
                _userData.postValue(database.userDao().getUserById(sharedPref.getUserId()!!))
            }
        }
    }
}