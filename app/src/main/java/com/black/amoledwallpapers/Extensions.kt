package com.black.amoledwallpapers

import com.black.amoledwallpapers.zionfiledownloader.DownloadFile
import com.black.amoledwallpapers.zionfiledownloader.ZionDownloadListener


fun DownloadFile.start(
    onFailed: ((message: String?) -> Unit)? = null,
    onPaused: ((message: String?) -> Unit)? = null,
    onPending: ((message: String?) -> Unit)? = null,
    onBusy: (() -> Unit)? = null,
    onSuccess: ((dataPath: String?) -> Unit)? = null
) {
    start(object : ZionDownloadListener {
        override fun onSuccess(dataPath: String?) {
            onSuccess?.invoke(dataPath)
        }

        override fun onFailed(message: String?) {
            onFailed?.invoke(message)
        }

        override fun onPaused(message: String?) {
            onPaused?.invoke(message)
        }

        override fun onPending(message: String?) {
            onPending?.invoke(message)
        }

        override fun onBusy() {
            onBusy?.invoke()
        }

    })
}
