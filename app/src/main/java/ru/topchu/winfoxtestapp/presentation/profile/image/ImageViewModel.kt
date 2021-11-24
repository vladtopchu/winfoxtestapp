package ru.topchu.winfoxtestapp.presentation.profile.image

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.topchu.winfoxtestapp.data.local.AppDatabase
import ru.topchu.winfoxtestapp.data.local.daos.UserDao
import ru.topchu.winfoxtestapp.data.local.entities.UserEntity
import ru.topchu.winfoxtestapp.data.remote.WinfoxApi.Companion.BASE_URL
import ru.topchu.winfoxtestapp.data.remote.dto.ImageDto
import ru.topchu.winfoxtestapp.data.remote.dto.LoginDto
import ru.topchu.winfoxtestapp.data.remote.dto.RegistrationDto
import ru.topchu.winfoxtestapp.domain.repository.WinfoxRepository
import ru.topchu.winfoxtestapp.utils.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class ImageViewModel @Inject constructor (
    @ApplicationContext private val applicationContext: Context,
    private val sharedPref: SharedPref,
    private val repository: WinfoxRepository
): ViewModel() {

    private val _status = MutableLiveData(ViewState<ImageDto>())
    val status = _status.asLiveData()

    private val _url: MutableLiveData<String?> = MutableLiveData(null)
    val url = _url.asLiveData()

    private val _uri: MutableLiveData<Uri?> = MutableLiveData(null)
    val uri = _uri.asLiveData()

    private val _path: MutableLiveData<String?> = MutableLiveData(null)
    val path = _path.asLiveData()

    private var job: Job? = null

    init {
        if(sharedPref.getUserProfilePicture() != null) {
            _url.postValue(sharedPref.getUserProfilePicture())
        }
    }

    fun saveImage(url: String){
        sharedPref.setUserProfilePicture(url)
    }

    fun setUri(string: Uri) {
       _uri.postValue(string)
    }

    fun uploadImage() {
        if(_uri.value != null){
            job?.cancel()
            job = viewModelScope.launch {
                val sourceFile = fileFromContentUri(applicationContext, _uri.value!!)
                val fileName: String = sourceFile.name
                val part = MultipartBody.Part.createFormData("file", fileName, sourceFile.asRequestBody())
                repository.uploadAvatar(part)
                    .onEach { result ->
                        when(result) {
                            is Resource.Success -> {
                                _status.postValue(status.value?.copy(
                                    response = result.data,
                                    isLoading = false
                                ))
                            }
                            is Resource.Loading -> {
                                _status.postValue(status.value?.copy(
                                    isLoading = true
                                ))
                            }
                            is Resource.Error -> {
                                _status.postValue(status.value?.copy(
                                    isLoading = false,
                                    errorMessage = result.message
                                ))
                            }
                        }
                    }.launchIn(this)
            }
        }
    }
}