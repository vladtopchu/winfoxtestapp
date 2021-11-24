package ru.topchu.winfoxtestapp.presentation.profile.info

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.topchu.winfoxtestapp.data.local.AppDatabase
import ru.topchu.winfoxtestapp.data.local.daos.UserDao
import ru.topchu.winfoxtestapp.data.local.entities.UserEntity
import ru.topchu.winfoxtestapp.data.remote.dto.LoginDto
import ru.topchu.winfoxtestapp.data.remote.dto.RegistrationDto
import ru.topchu.winfoxtestapp.data.remote.dto.UpdateProfileDto
import ru.topchu.winfoxtestapp.domain.repository.WinfoxRepository
import ru.topchu.winfoxtestapp.utils.Resource
import ru.topchu.winfoxtestapp.utils.SharedPref
import ru.topchu.winfoxtestapp.utils.ViewState
import ru.topchu.winfoxtestapp.utils.asLiveData
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class InfoViewModel @Inject constructor (
    private val sharedPref: SharedPref,
    private val userDao: UserDao,
    private val repository: WinfoxRepository
): ViewModel() {

    private val _userData: MutableLiveData<UserEntity> = MutableLiveData(null)
    val userData = _userData.asLiveData()

    private val _state = MutableLiveData(ViewState<UpdateProfileDto>())
    val state = _state.asLiveData()

    private val _prefs: MutableLiveData<MutableList<String>> = MutableLiveData(mutableListOf())
    val prefs = _prefs.asLiveData()

    private var job: Job? = null

    init {
        viewModelScope.launch {
            val user = userDao.getUserById(sharedPref.getUserId()!!)
            _userData.postValue(user)
            if(user.preferences.isNotEmpty()) {
                _prefs.postValue(user.preferences.toMutableList())
            }
        }
    }

    fun addToPrefs(string: String){
        if(!_prefs.value!!.contains(string)){
            _prefs.value!!.add(string)
            _prefs.postValue(_prefs.value)
        }
    }

    fun removeFromPrefs(string: String) {
        if(_prefs.value!!.contains(string)){
            _prefs.value!!.remove(string)
            _prefs.postValue(_prefs.value)
        }
    }

    fun proceedUpdate(userUpdateProfileDto: UpdateProfileDto) {
        job?.cancel()
        job = viewModelScope.launch {
            Timber.d(userUpdateProfileDto.toString())
            repository.updateProfile(userUpdateProfileDto)
                .onEach { result ->
                    when(result) {
                        is Resource.Success -> {
                            _state.postValue(state.value?.copy(
                                response = result.data,
                                isLoading = false
                            ))
                            val entity = result.data!!.toEntity()
                            entity.id = userData.value!!.id
                            val test = userDao.updateUser(entity)
                            Timber.d("Db updated?")
                            Timber.d(test.toString())
                            if(test == 1) {
                                sharedPref.setUserId(result.data.id!!)
                            }
                        }
                        is Resource.Loading -> {
                            _state.postValue(state.value?.copy(
                                isLoading = true
                            ))
                        }
                        is Resource.Error -> {
                            _state.postValue(state.value?.copy(
                                isLoading = false,
                                errorMessage = result.message
                            ))
                        }
                    }
                }.launchIn(this)
        }
    }

}