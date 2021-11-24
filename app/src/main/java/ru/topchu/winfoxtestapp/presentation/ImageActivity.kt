package ru.topchu.winfoxtestapp.presentation

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import ru.topchu.winfoxtestapp.BuildConfig
import ru.topchu.winfoxtestapp.databinding.ActivityImageBinding
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.io.OutputStream

@AndroidEntryPoint
class ImageActivity : AppCompatActivity() {

    private var permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private lateinit var binding: ActivityImageBinding
    private var requestPermissionLauncher : ActivityResultLauncher<Array<String>>? = null
    private var selectImageLauncher: ActivityResultLauncher<String>? = null
    private var cameraImageLauncher: ActivityResultLauncher<Uri>? = null

    private var tempImageUri: Uri? = null
    private var tempImagePath: String? = null

    private val viewModel: ImageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if(it.any { entry -> !entry.value }) {
                Timber.d("Permission deniend for: ${it.toList().filter { predicate -> !predicate.second }}")
            } else {
                Timber.d("Permission given!")
            }
        }

        viewModel.uri.observe(this) {
            if(it != null) {
                Timber.d(it.path)
                binding.selectedImage.setImageURI(it)
            }
        }

        cameraImageLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if(it) {
                viewModel.setUri(tempImageUri!!)
            }
        }

        binding.makePhoto.setOnClickListener {
            if(permissions.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
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

        selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            viewModel.setUri(it)
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
            requestPermissionLauncher!!.launch(permissions)
        } else {
            selectImageLauncher!!.launch("image/*")
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