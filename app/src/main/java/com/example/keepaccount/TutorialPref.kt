package com.example.keepaccount

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class TutorialPref(context: Context) {
    companion object {
        private const val PREF_NAME = "tutorial_pref"
        private const val KEY_IS_SHOWN = "key_is_tutorial_shown"
    }

    private val pref: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun isTutorialShown(): Boolean {
        return pref.getBoolean(KEY_IS_SHOWN, false)
    }

    fun setTutorialShown() {
        pref.edit { putBoolean(KEY_IS_SHOWN, true) }
    }
}
