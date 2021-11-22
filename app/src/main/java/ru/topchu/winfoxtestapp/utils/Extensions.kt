package ru.topchu.winfoxtestapp.utils

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.asLiveData() = this as LiveData<T>
fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

