package ru.topchu.winfoxtestapp.presentation.auth.registration

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.topchu.winfoxtestapp.data.remote.dto.LoginDto
import ru.topchu.winfoxtestapp.data.remote.dto.RegistrationDto
import ru.topchu.winfoxtestapp.domain.repository.WinfoxRepository
import ru.topchu.winfoxtestapp.utils.Resource
import ru.topchu.winfoxtestapp.utils.ViewState
import ru.topchu.winfoxtestapp.utils.asLiveData
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: WinfoxRepository
): ViewModel() {

    private val _state = MutableLiveData(ViewState<RegistrationDto>())
    val state = _state.asLiveData()

    private var job: Job? = null

    fun proceedRegistration(email: String, firstname: String, lastname: String, password: String) {
        job?.cancel()
        job = viewModelScope.launch {
            repository.registerUser(RegistrationDto(email, firstname, lastname, password))
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