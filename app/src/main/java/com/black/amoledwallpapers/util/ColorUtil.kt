package com.black.amoledwallpapers.util

import android.graphics.Color

object ColorUtil {

    fun parseList(value: String?): IntArray {
        value?.trim('#')?.split("#")?.let {
            val array = IntArray(it.size)

            it.forEachIndexed { index, s ->
                array[index] = Color.parseColor("#AA$s")
            }
            return array
        }
        return intArrayOf()
    }
}