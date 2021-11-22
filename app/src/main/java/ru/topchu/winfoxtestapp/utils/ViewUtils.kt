package ru.topchu.winfoxtestapp.utils

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import ru.topchu.winfoxtestapp.R

fun isValidEmail(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()

fun clickListener(field: EditText) = View.OnClickListener {
    if(field.transformationMethod == HideReturnsTransformationMethod.getInstance()) {
        (it as ImageView).setImageResource(R.drawable.ic_eye_closed)
        field.transformationMethod = PasswordTransformationMethod.getInstance()
    } else {
        (it as ImageView).setImageResource(R.drawable.ic_eye_open)
        field.transformationMethod = HideReturnsTransformationMethod.getInstance()
    }
    field.setSelection(field.text.length)
}