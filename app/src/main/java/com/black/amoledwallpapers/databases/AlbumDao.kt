package com.black.amoledwallpapers.databases

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.black.amoledwallpapers.entities.Album

@Dao
interface AlbumDao {
    @Query("SELECT * FROM albums")
    fun getAlbum(): List<Album>

    @Query("SELECT * FROM albums WHERE id = :albumId")
    fun getAlbum(albumId: Long): Album

    @Insert(onConflict = REPLACE)
    fun insert(albumList: List<Album>)
}