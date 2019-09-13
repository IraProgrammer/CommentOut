package ru.trmedia.trbtlservice.comment.data

import android.content.Context
import android.content.SharedPreferences


class AppPreferences(context: Context) {

    private val preferences: SharedPreferences

    init {
        this.preferences =
            context.getSharedPreferences(APP_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
    }

    fun getString(key: String): String? {
        return preferences.getString(key, null)
    }

    fun putString(key: String, value: String) {
        val editor = preferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun clear() {
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        val APP_PREFERENCES_FILE_NAME = "userdata"
        val USER_ID = "userID"
        val TOKEN = "token"
        val PROFILE_PIC = "profile_pic"
        val USER_NAME = "username"
    }
}