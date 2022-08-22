package com.black.amoledwallpapers.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "albums")
class Album() : Parcelable {
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    var id: Long = 0

    @ColumnInfo(name = "title")
    @SerializedName("title")
    var title: String = ""

    @ColumnInfo(name = "image")
    @SerializedName("image")
    var image: String = ""

    @ColumnInfo(name = "color")
    @SerializedName("color")
    var color: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        title = parcel.readString()
        image = parcel.readString()
        color = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(title)
        parcel.writeString(image)
        parcel.writeString(color)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Album> {
        override fun createFromParcel(parcel: Parcel): Album {
            return Album(parcel)
        }

        override fun newArray(size: Int): Array<Album?> {
            return arrayOfNulls(size)
        }
    }


}
