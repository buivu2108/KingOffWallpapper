package com.black.amoledwallpapers.widgets

import android.app.Dialog
import android.content.Context
import com.black.amoledwallpapers.R

class LoadingDialog(context: Context) : Dialog(context) {

    init {
        setContentView(R.layout.dialog_loading)
        window.setBackgroundDrawableResource(android.R.color.transparent)
    }
}