package ru.topchu.winfoxtestapp.utils

import android.text.Editable
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.File

fun <T> MutableLiveData<T>.asLiveData() = this as LiveData<T>
fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

fun File.getMimeType(fallback: String = "image/*"): String {
    return MimeTypeMap.getFileExtensionFromUrl(toString())
        ?.run { MimeTypeMap.getSingleton().getMimeTypeFromExtension(lowercase()) }
        ?: fallback // You might set it to */*
}
