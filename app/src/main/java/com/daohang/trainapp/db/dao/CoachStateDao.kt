package com.daohang.trainapp.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.daohang.trainapp.db.models.CoachStateModel

@Dao
interface CoachStateDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoachLogin(model: CoachStateModel)

    @Query("delete from coach_state where id=:id")
    fun delete(id: Int)

    @Query("select * from coach_state where hasUploaded = 0 order by time")
    fun findCoachUnUploaded(): LiveData<List<CoachStateModel>>

    @Query("delete from coach_state")
    fun delete()
}