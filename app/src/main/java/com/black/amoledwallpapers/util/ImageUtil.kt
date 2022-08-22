package com.black.amoledwallpapers.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.net.MalformedURLException
import java.net.URL

object ImageUtil {

    fun loadImage(uri: Any, imageView: ImageView, onComplete: (() -> Unit)? = null) {
        LibGlide.with(imageView.context)
            .load(uri)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onComplete?.invoke()
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onComplete?.invoke()
                    return true
                }

            })
            .into(imageView)
    }

    fun loadImage(url: String, color: Int, imageView: ImageView, onComplete: (() -> Unit)? = null) {
        LibGlide.with(imageView)
            .load(url)
            .placeholder(ColorDrawable(color))
            .transition(DrawableTransitionOptions.withCrossFade())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onComplete?.invoke()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onComplete?.invoke()
                    return false
                }

            })
            .into(imageView)
    }

    fun loadBlurImage(context: Context, uri: Any, imageView: ImageView) {
        LibGlide.with(context)
            .load(uri)
            .transform(BlurTransformation(25, 4))
            .into(imageView)
    }

    fun getImageBitmap(srcPath: String): Bitmap {
        return BitmapFactory.decodeFile(srcPath)
    }

    fun getFileNameFromUrl(url: String?): String {
        if (url == null) {
            return ""
        }
        try {
            val resource = URL(url)
            val host = resource.host
            if (host.length > 0 && url.endsWith(host)) {
                // handle ...example.com
                return ""
            }
        } catch (e: MalformedURLException) {
            return ""
        }

        val startIndex = url.lastIndexOf('/') + 1
        val length = url.length

        // find end index for ?
        var lastQMPos = url.lastIndexOf('?')
        if (lastQMPos == -1) {
            lastQMPos = length
        }

        // find end index for #
        var lastHashPos = url.lastIndexOf('#')
        if (lastHashPos == -1) {
            lastHashPos = length
        }

        // calculate the end index
        val endIndex = Math.min(lastQMPos, lastHashPos)
        return url.substring(startIndex, endIndex)
    }
}