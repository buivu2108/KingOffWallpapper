package com.black.amoledwallpapers

import com.google.gson.annotations.SerializedName

class ApiResponse<T> {

    @SerializedName("status")
    var status: Int = 0

    @SerializedName("success")
    var success: Boolean = false

    @SerializedName("data")
    var data: T? = null
}