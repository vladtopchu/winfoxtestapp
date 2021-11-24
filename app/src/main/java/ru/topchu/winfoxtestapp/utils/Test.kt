package ru.topchu.winfoxtestapp.utils

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import timber.log.Timber
import java.io.File

fun getFileFromUri(uri: Uri, context: Context): File? {
    if (uri.path == null) {
        return null
    }
    var realPath = String()
    val databaseUri: Uri
    val selection: String?
    val selectionArgs: Array<String>?
    if (uri.path!!.contains("/document/image:")) {
        databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        selection = "_id=?"
        selectionArgs = arrayOf(DocumentsContract.getDocumentId(uri).split(":")[1])
    } else {
        databaseUri = uri
        selection = null
        selectionArgs = null
    }
    try {
        val column = "_data"
        val projection = arrayOf(column)
        val cursor = context.contentResolver.query(
            databaseUri,
            projection,
            selection,
            selectionArgs,
            null
        )
        cursor?.let {
            if (it.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(column)
                realPath = cursor.getString(columnIndex)
            }
            cursor.close()
        }
    } catch (e: Exception) {
        Timber.i("GetFileUri Exception:", e.message ?: "")
    }
    val path = if (realPath.isNotEmpty()) realPath else {
        when {
            uri.path!!.contains("/document/raw:") -> uri.path!!.replace(
                "/document/raw:",
                ""
            )
            uri.path!!.contains("/document/primary:") -> uri.path!!.replace(
                "/document/primary:",
                "/storage/emulated/0/"
            )
            else -> return null
        }
    }
    return File(path)
}