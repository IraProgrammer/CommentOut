package ru.trmedia.trbtlservice.comment.data.network

import android.content.Context
import android.content.SharedPreferences


class AppPreferences(context: Context) {

    private val preferences = context.getSharedPreferences(APP_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

    fun getString(key: String): String? {
        return preferences.getString(key, null)
    }

    fun putString(key: String, value: String) {
        val editor = preferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getBoolean(key: String): Boolean {
        return preferences.getBoolean(key, true)
    }

    fun putBoolean(key: String, value: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getSet(key: String): MutableSet<String> {
        return preferences.getStringSet(key, null) ?: HashSet()
    }

    fun addToSet(key: String, value: String) {
        val editor = preferences.edit()
        val set = getSet(key)
        set.add(value)
        editor.putStringSet(key, set)
        editor.apply()
    }

    fun clearSet(key: String) {
        val editor = preferences.edit()
        editor.putStringSet(key, HashSet())
        editor.apply()
    }

    fun clear() {
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        val APP_PREFERENCES_FILE_NAME = "userdata"
        val USER_NAME = "username"
        val USERS_SET = "usersSet"
        val COMMENTS_SET = "commentsSet"
        val PUNISHMENT_SET = "punishmentSet"
        val SHOW_SAFE = "showSafe"
        val SHOW_RULES = "showRules"
    }
}