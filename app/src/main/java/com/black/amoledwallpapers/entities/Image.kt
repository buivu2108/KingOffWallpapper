package com.black.amoledwallpapers.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.black.amoledwallpapers.App
import com.black.amoledwallpapers.R
import com.google.gson.annotations.SerializedName

@Entity(tableName = "images")
open class Image {
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    var id: String = ""

    @ColumnInfo(name = "name")
    var name: String? = null

    @ColumnInfo(name = "album_id")
    var albumId: Long = 0

    @ColumnInfo(name = "url")
    var url: String = ""

    @ColumnInfo(name = "thumb")
    var thumb: String = ""

    val color: String
    get() {
        val colors = App.get().resources.getStringArray(R.array.palette_colors)
        return colors.random()
    }

}