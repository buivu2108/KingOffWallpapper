package com.black.amoledwallpapers.databases

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.black.amoledwallpapers.entities.Favorite

@Dao
interface FavDao {
    @Query("SELECT * FROM favorites ORDER BY timestamp DESC")
    fun getImages(): LiveData<List<Favorite>>

    @Query("SELECT * FROM favorites WHERE id = :imageId")
    fun getImage(imageId: String): Favorite?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(image: Favorite)

    @Query("DELETE FROM favorites WHERE id = :id")
    fun delete(id: String)

}