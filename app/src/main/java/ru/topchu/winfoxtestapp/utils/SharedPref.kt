package ru.topchu.winfoxtestapp.utils

import android.content.Context
import android.content.SharedPreferences
import ru.topchu.winfoxtestapp.utils.Constants.SPKEY_FILENAME
import ru.topchu.winfoxtestapp.utils.Constants.SPKEY_USERID
import ru.topchu.winfoxtestapp.utils.Constants.SPKEY_USERPICTURE

class SharedPref(context: Context) {

    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SPKEY_FILENAME, Context.MODE_PRIVATE)

    fun getUserId(): String? = sharedPreferences.getString(SPKEY_USERID, null)

    fun setUserId(id: String) {
        sharedPreferences.edit().putString(SPKEY_USERID, id).apply()
    }

    fun wipeUserId() {
        sharedPreferences.edit().putString(SPKEY_USERID, null).apply()
    }

    fun getUserProfilePicture(): String? = sharedPreferences.getString(SPKEY_USERPICTURE, null)

    fun setUserProfilePicture(filename: String) {
        sharedPreferences.edit().putString(SPKEY_USERPICTURE, filename).apply()
    }

    fun wipeUserProfilePicture() {
        sharedPreferences.edit().putString(SPKEY_USERPICTURE, null).apply()
    }
}
