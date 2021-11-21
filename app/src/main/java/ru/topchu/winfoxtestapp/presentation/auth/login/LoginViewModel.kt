package ru.topchu.winfoxtestapp.presentation.auth.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.topchu.winfoxtestapp.data.remote.dto.LoginDto
import ru.topchu.winfoxtestapp.domain.repository.WinfoxRepository
import ru.topchu.winfoxtestapp.utils.Resource
import ru.topchu.winfoxtestapp.utils.asLiveData
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: WinfoxRepository
): ViewModel() {

    private val _state = MutableLiveData(LoginState())
    val state = _state.asLiveData()

    private var job: Job? = null

    fun proceedLogin(email: String, password: String) {
        job?.cancel()
        job = viewModelScope.launch {
            repository.checkLogin(LoginDto(email, password))
                .onEach { result ->
                    when(result) {
                        is Resource.Success -> {
                            _state.postValue(state.value?.copy(
                                response = result.data,
                                isLoading = false
                            ))
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