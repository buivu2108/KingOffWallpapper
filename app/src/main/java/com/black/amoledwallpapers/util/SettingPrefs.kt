package com.black.amoledwallpapers.util

import android.content.Context
import com.black.amoledwallpapers.App
import com.message.privacy.extensions.get
import com.message.privacy.extensions.set

class SettingPrefs(context: Context) {

    private val prefs = context.getSharedPreferences("setting", Context.MODE_PRIVATE)

    var isCanShowRate: Boolean
        get() = prefs[SHOW_RATE, true]
        set(value) {
            prefs[SHOW_RATE] = value
        }

    companion object {

        private const val SHOW_RATE = "show.rate"

        private val pref = SettingPrefs(App.get())
        fun get() = pref
    }
}