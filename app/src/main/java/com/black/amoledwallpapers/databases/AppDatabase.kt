package com.black.amoledwallpapers.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.black.amoledwallpapers.entities.Album
import com.black.amoledwallpapers.entities.Favorite
import com.black.amoledwallpapers.entities.History
import com.black.amoledwallpapers.entities.Image

@Database(entities = [Album::class, Image::class, Favorite::class, History::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getAlbum(): AlbumDao
    abstract fun getImage(): ImageDao
    abstract fun getFav(): FavDao
    abstract fun getHistory():HistoryDao

}