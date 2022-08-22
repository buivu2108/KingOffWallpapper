package com.black.amoledwallpapers.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.amoled.ControllMyAd
import com.amoled.utilAds.ListenerCloseAd
import com.black.amoledwallpapers.Const


object Utils {
    const val SERVER_URL = "https://i.imgur.com/"
    fun openGooglePlay(context: Context) {
        val appPackageName = context.packageName // getPackageName() from Context or Activity object
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }

    }

    fun showAdFullScreen(activity: Activity, listenerCloseAd: () -> Unit) {
        if (Const.SHOW_ADS) {
            ControllMyAd.getInstance().showAdFullScreen(activity, ControllMyAd.GROUP_KEY_1, object : ListenerCloseAd() {
                override fun closeAd() {
                    super.closeAd()
                    if (!activity.isFinishing) {
                        listenerCloseAd.invoke()
                    }
                }
            })
        } else {
            listenerCloseAd.invoke()
        }
    }
}