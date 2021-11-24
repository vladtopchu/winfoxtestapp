package ru.topchu.winfoxtestapp.presentation.profile.image

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import dagger.hilt.android.AndroidEntryPoint
import ru.topchu.winfoxtestapp.BuildConfig
import ru.topchu.winfoxtestapp.data.remote.WinfoxApi.Companion.BASE_URL
import ru.topchu.winfoxtestapp.databinding.ActivityImageBinding
import ru.topchu.winfoxtestapp.utils.Resource
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ImageActivity : AppCompatActivity() {

    private var permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private lateinit var binding: ActivityImageBinding
    private var requestPermissionLauncher : ActivityResultLauncher<Array<String>>? = null
    private var selectImageLauncher: ActivityResultLauncher<String>? = null
    private var cameraImageLauncher: ActivityResultLauncher<Uri>? = null

    private var tempImageUri: Uri? = null

    @Inject
    lateinit var glide: RequestManager

    private val viewModel: ImageViewModel by viewModels()

    private var lastAction = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if(it.any { entry -> !entry.value }) {
                Timber.d("Permission deniend for: ${it.toList().filter { predicate -> !predicate.second }}")
            } else {
                when(lastAction){
                    0 -> makePhoto()
                    1 -> selectImage()
                    else -> throw Exception("Unrecognized action")
                }
                Timber.d("Permission given!")
            }
        }

        cameraImageLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if(it) {
                viewModel.setUri(tempImageUri!!)
            }
        }

        selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if(it != null) {
                viewModel.setUri(it)
            }
        }

        viewModel.url.observe(this) {
            if(it !=null) {
                binding.progressCircular.visibility = View.VISIBLE
                glide
                    .load(it)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.progressCircular.visibility = View.GONE
                            Toast.makeText(this@ImageActivity, "Не удалось загрузить фото с сервера", Toast.LENGTH_LONG).show()
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.progressCircular.visibility = View.GONE
                            binding.currentPhoto.visibility = View.VISIBLE
                            return false
                        }

                    })
                    .into(binding.selectedImage)
            }
        }

        viewModel.uri.observe(this) {
            if(it != null) {
                if(!binding.uploadImage.isEnabled)
                    binding.uploadImage.isEnabled = true
                if(binding.currentPhoto.visibility == View.VISIBLE)
                    binding.currentPhoto.visibility = View.GONE
                glide.load(it).into(binding.selectedImage)
            } else {
                binding.uploadImage.isEnabled = false
            }
        }

        viewModel.status.observe(this) {
            if(it.isLoading) {
                binding.progressCircular.visibility = View.VISIBLE
                binding.uploadImage.isEnabled = false
            } else {
                binding.progressCircular.visibility = View.GONE
                binding.uploadImage.isEnabled = true
                if(it.errorMessage != null) {
                    Toast.makeText(this, "Произошла ошибка: ${it.errorMessage}", Toast.LENGTH_LONG).show()
                } else if(it.response != null) {
                    Toast.makeText(this, "Фотография успешно обновлена! ${it.response.filename}", Toast.LENGTH_SHORT).show()
                    viewModel.saveImage("${BASE_URL}getImage?filename=${it.response.filename}")
                }
            }
        }

        binding.makePhoto.setOnClickListener {
            makePhoto()
        }

        binding.selectImage.setOnClickListener {
            selectImage()
        }

        binding.uploadImage.setOnClickListener {
            viewModel.uploadImage()
        }
    }

    private fun selectImage(){
        if(permissions.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
            lastAction = 1
            requestPermissionLauncher!!.launch(permissions)
        } else {
            selectImageLauncher!!.launch("image/*")
        }
    }

    private fun makePhoto(){
        if(permissions.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
            lastAction = 0
            requestPermissionLauncher!!.launch(permissions)
        } else {
            lifecycleScope.launchWhenStarted {
                getTmpFileUri().let { uri ->
                    tempImageUri = uri
                    cameraImageLauncher!!.launch(uri)
                }
            }
        }
    }

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".jpg", filesDir).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(applicationContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }
}