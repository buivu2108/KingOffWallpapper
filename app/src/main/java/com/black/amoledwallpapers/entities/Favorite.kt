package com.black.amoledwallpapers.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "favorites")
class Favorite {
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    var id: String = ""

    @ColumnInfo(name = "url")
    var url: String = ""

    @ColumnInfo(name = "color")
    var color: String = ""

    @ColumnInfo(name = "timestamp")
    var timestamp: Long = 0
}