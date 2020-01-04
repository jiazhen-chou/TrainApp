package com.daohang.trainapp.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.daohang.trainapp.db.models.TrainPreferenceModel

@Dao
interface TrainPreferenceDao {

    @Query("select * from trainPreference limit 1")
    fun getTrainPreference(): TrainPreferenceModel?

    @Query("select * from trainPreference limit 1")
    fun getTrainPreferenceLiveData(): LiveData<TrainPreferenceModel>

    @Insert
    fun insertTrainPreference(model: TrainPreferenceModel)

    /**
     * 清空表
     */
    @Query("delete from trainPreference")
    fun deleteTrainPreference()
}