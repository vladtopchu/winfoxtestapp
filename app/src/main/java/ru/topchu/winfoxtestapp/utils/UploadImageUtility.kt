package ru.topchu.winfoxtestapp.utils

import android.content.ContentProvider
import android.content.Context
import android.net.Uri
import android.os.FileUtils
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import ru.topchu.winfoxtestapp.data.remote.WinfoxApi
import ru.topchu.winfoxtestapp.data.remote.dto.ImageDto
import timber.log.Timber
import java.io.*

class UploadImageUtility(var context: Context, var listener: ImageUploadProgressListener) {

    var serverURL: String = "${WinfoxApi.BASE_URL}uploadAvatar"
    private val client = OkHttpClient()

    suspend fun uploadFile(sourceUri: Uri) {
        val sourceFile = fileFromContentUri(context, sourceUri)
        Timber.d(sourceFile.toString())
        val fileName: String = sourceFile!!.name
        listener.onLoadingStarted()
        try {
            val requestBody: RequestBody =
                MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("file", fileName, sourceFile.asRequestBody())
                    .build()
            val request: Request = Request.Builder().url(serverURL).post(requestBody).build()

            val response: Response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val filenameResponse = Gson().fromJson(response.body!!.string(), ImageDto::class.java)
                listener.onSuccess(filenameResponse.filename)
            } else {
                Timber.d("File upload: failed")
                listener.onError(response.message)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            Timber.d("File upload: failed")
            listener.onError(ex.message.toString())
        }
    }

    interface ImageUploadProgressListener {
        fun onLoadingStarted()
        fun onError(message: String)
        fun onSuccess(imagePath: String)
    }

    fun fileFromContentUri(context: Context, contentUri: Uri): File {
        val fileExtension = getFileExtension(context, contentUri)
        val fileName = "temp_file" + if (fileExtension != null) ".$fileExtension" else ""

        val tempFile = File(context.cacheDir, fileName).apply {
            createNewFile()
            deleteOnExit()
        }

        try {
            val oStream = FileOutputStream(tempFile)
            val inputStream = context.contentResolver.openInputStream(contentUri)

            inputStream?.let {
                copy(inputStream, oStream)
            }

            oStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return tempFile
    }

    private fun getFileExtension(context: Context, uri: Uri): String? {
        val fileType: String? = context.contentResolver.getType(uri)
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
    }

    @Throws(IOException::class)
    private fun copy(source: InputStream, target: OutputStream) {
        val buf = ByteArray(8192)
        var length: Int
        while (source.read(buf).also { length = it } > 0) {
            target.write(buf, 0, length)
        }
    }

}