package com.black.amoledwallpapers

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.message.privacy.extensions.doAsync
import com.black.amoledwallpapers.databases.*
import com.black.amoledwallpapers.entities.Album
import com.black.amoledwallpapers.entities.Favorite
import com.black.amoledwallpapers.entities.History
import com.black.amoledwallpapers.entities.Image

class AppRepository(context: Context) {
    private val album: AlbumDao
    private val image: ImageDao
    private val favorite: FavDao
    private val history: HistoryDao

    init {
        val database = Room.databaseBuilder(context, AppDatabase::class.java, "superhero")
            .build()
        album = database.getAlbum()
        image = database.getImage()
        favorite = database.getFav()
        history = database.getHistory()
    }

    fun getAlbum(): List<Album> {
        return album.getAlbum()
    }

    fun getAlbum(albumId: Long): Album {
        return album.getAlbum(albumId)
    }

    fun getAllImages(): List<Image> {
        return image.getImages()
    }

    fun getImages(albumId: Long): List<Image> {
        return image.getImages(albumId)
    }

    fun getImages(): List<Image> {
        return image.getImages()
    }

    fun getImage(imageId: String): Image? {
        return image.getImage(imageId)
    }

    fun insertAlbum(list: List<Album>) {
        album.insert(list)
    }

    fun insertImage(list: List<Image>) {
        image.insert(list)
    }

    fun addHistory(image: Image) {
        val his = History()
        his.id = image.id
        his.url = image.url
        his.timestamp = System.currentTimeMillis()
        history.insert(his)
    }

    fun addFav(image: Image) {
        doAsync {
            val fav = Favorite()
            fav.id = image.id
            fav.url = image.url
            fav.color = image.color
            fav.timestamp = System.currentTimeMillis()
            favorite.insert(fav)
        }
    }

    fun liveHistory(): LiveData<List<History>> {
        return history.getImages()
    }

    fun liveFavorite(): LiveData<List<Favorite>> {
        return favorite.getImages()
    }

    fun liveHome(): LiveData<List<Image>> {
        return image.liveImages()
    }

    fun removeFav(id: String) {
        doAsync {
            favorite.delete(id)
        }
    }

    fun getFav(imageId: String): Favorite? {
        return favorite.getImage(imageId)

    }

    fun loadImage(list: Array<Long>): List<Image>? {
        return image.getImage(list)
    }

//    fun find(key: String): List<Image> {
//        return image.find(key)
//    }

    companion object {
        private val repository by lazy { AppRepository(App.get()) }

        fun get() = repository
    }
}