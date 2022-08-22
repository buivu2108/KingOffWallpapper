package com.black.amoledwallpapers.databases

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.black.amoledwallpapers.entities.Image

@Dao
interface ImageDao {

    @Query("SELECT * FROM images")
    fun getImages(): List<Image>

    @Query("SELECT * FROM images")
    fun liveImages(): LiveData<List<Image>>

    @Query("SELECT * FROM images WHERE album_id = :albumId")
    fun getImages(albumId: Long): List<Image>

    @Query("SELECT * FROM images WHERE id = :imageId")
    fun getImage(imageId: String): Image?

    @Query("SELECT * FROM images WHERE id IN (:imageList)")
    fun getImage(imageList: Array<Long>): List<Image>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(albumList: List<Image>)
//    @Query("SELECT * FROM images WHERE name LIKE '%' || :key || '%' ORDER BY album_id")
//    fun find(key: String): List<Image>
}