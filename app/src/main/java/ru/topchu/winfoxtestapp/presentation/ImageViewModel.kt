package ru.topchu.winfoxtestapp.presentation

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
import ru.topchu.winfoxtestapp.data.local.entities.UserEntity
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
    private val database: AppDatabase
): ViewModel() {

    private val _uri: MutableLiveData<Uri?> = MutableLiveData(null)
    val uri = _uri.asLiveData()

    private val _path: MutableLiveData<String?> = MutableLiveData(null)
    val path = _path.asLiveData()

    var uploadImageUtility: UploadImageUtility = UploadImageUtility(applicationContext, object : UploadImageUtility.ImageUploadProgressListener {
        override fun onLoadingStarted() {
            Timber.d("Loading started")
        }

        override fun onError(message: String) {
            Timber.d(message)
        }

        override fun onSuccess(imagePath: String) {
            Timber.d(imagePath)
        }
    })

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