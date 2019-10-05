package ru.islab.evilcomments.data

import android.content.Context


class AppPreferences(context: Context) {

    private val preferences = context.getSharedPreferences(APP_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

    fun getString(key: String): String {
        return preferences.getString(key, null) ?: ""
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
        const val APP_PREFERENCES_FILE_NAME = "userdata"
        const val USER_NAME = "username"
        const val USERS_SET = "usersSet"
        const val COMMENTS_SET = "commentsSet"
        const val PUNISHMENT_SET = "punishmentSet"
        const val SHOW_SAFE = "showSafe"
        const val SHOW_RULES = "showRules"
        const val COMMENT = "comment"
        const val PUNISHMENT = "punishment"
        const val USER_IN_CIRCLE = "user_in_circle"
        const val PHOTO = "photo"
        const val ROUND = "raund"
        const val NEED_NEW_GAME = "needNewGame"
        const val POINTS = "points"
    }
}