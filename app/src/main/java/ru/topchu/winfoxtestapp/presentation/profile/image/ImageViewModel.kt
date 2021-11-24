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
import ru.topchu.winfoxtestapp.data.local.AppDatabase
import ru.topchu.winfoxtestapp.data.local.daos.UserDao
import ru.topchu.winfoxtestapp.data.local.entities.UserEntity
import ru.topchu.winfoxtestapp.data.remote.WinfoxApi.Companion.BASE_URL
import ru.topchu.winfoxtestapp.data.remote.dto.LoginDto
import ru.topchu.winfoxtestapp.data.remote.dto.RegistrationDto
import ru.topchu.winfoxtestapp.domain.repository.WinfoxRepository
import ru.topchu.winfoxtestapp.utils.Resource
import ru.topchu.winfoxtestapp.utils.SharedPref
import ru.topchu.winfoxtestapp.utils.UploadImageUtility
import ru.topchu.winfoxtestapp.utils.asLiveData
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class ImageViewModel @Inject constructor (
    @ApplicationContext applicationContext: Context,
    private val sharedPref: SharedPref,
    private val userDao: UserDao
): ViewModel() {

    private val _status: MutableLiveData<Resource<String>> = MutableLiveData(Resource.Idle())
    val status = _status.asLiveData()

    private val _url: MutableLiveData<String?> = MutableLiveData(null)
    val url = _url.asLiveData()

    private val _uri: MutableLiveData<Uri?> = MutableLiveData(null)
    val uri = _uri.asLiveData()

    private val _path: MutableLiveData<String?> = MutableLiveData(null)
    val path = _path.asLiveData()

    init {
        if(sharedPref.getUserProfilePicture() != null) {
            _url.postValue(sharedPref.getUserProfilePicture())
        }
    }

    private var uploadImageUtility: UploadImageUtility = UploadImageUtility(applicationContext, object : UploadImageUtility.ImageUploadProgressListener {
        override fun onLoadingStarted() {
            _status.postValue(Resource.Loading())
        }

        override fun onError(message: String) {
            _status.postValue(Resource.Error(message = message))
        }

        override fun onSuccess(imageServerPath: String) {
            _status.postValue(Resource.Success("${BASE_URL}getImage?filename=$imageServerPath"))
        }
    })

    fun saveImage(url: String){
        sharedPref.setUserProfilePicture(url)
    }

    fun setUri(string: Uri) {
       _uri.postValue(string)
    }

    fun uploadImage() {
        if(_uri.value != null){
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    uploadImageUtility.uploadFile(_uri.value!!)
                }
            }
        } else {
            Timber.d("Выберите фото!")
        }
    }
}