package com.roadpass.avid.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import java.lang.ref.WeakReference
import java.util.*

@SuppressLint("HardwareIds")
fun getInstallationId(context: Context?) {
    val uniqueId = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)

    val contextWeakReference: WeakReference<Context> = WeakReference<Context>(context)
    if (contextWeakReference.get() != null) {
        val prefs = getDefaultSharedPreferences(contextWeakReference.get())
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString("uuid", uniqueId)
        editor.apply()
    }
}