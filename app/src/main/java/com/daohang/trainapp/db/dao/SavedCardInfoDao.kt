package com.daohang.trainapp.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.daohang.trainapp.db.models.SavedCardInfo

@Dao
interface SavedCardInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(model: SavedCardInfo)

    @Query("delete from saved_card_info")
    fun delete()

    @Query("select * from saved_card_info where type=0 limit 1")
    fun getStudentInfo(): LiveData<SavedCardInfo>

    @Query("select * from saved_card_info where type=1 limit 1")
    fun getCoachInfo(): LiveData<SavedCardInfo>

    @Query("select * from saved_card_info limit 2")
    fun getUnCopmpleteInfo(): LiveData<List<SavedCardInfo>>

    @Query("select count(*) from saved_card_info")
    fun getUpCompleteCount(): LiveData<Long>
}