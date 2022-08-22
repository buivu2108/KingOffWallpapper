package com.black.amoledwallpapers.databases

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.black.amoledwallpapers.entities.History

@Dao
interface HistoryDao {
    @Query("SELECT * FROM histories ORDER BY timestamp DESC LIMIT 24")
    fun getImages(): LiveData<List<History>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(image: History)
}