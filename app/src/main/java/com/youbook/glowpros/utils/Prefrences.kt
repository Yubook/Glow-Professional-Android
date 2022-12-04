package com.youbook.glowpros.utils

import android.app.Activity
import android.content.Context

object Prefrences {

    fun savePreferencesString(context: Context, key: String, value: String): String {
        val sharedPreferences = context.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.commit()

        return key
    }

    fun getPreferences(context: Context, key: String): String? {

        val sharedPreferences = context.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, "")

    }

    fun removePreferences(context: Activity, key: String) {

        val sharedPreferences = context.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(key)
        editor.commit()

    }
    fun clearPreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.commit()
    }
}