package com.daohang.trainapp.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.daohang.trainapp.db.models.PreferenceModel

@Dao
interface PreferenceDao{

    /**
     * 查询唯一一条数据
     */
    @Query("select * from preference limit 1")
    fun getPreferenceLiveData(): LiveData<List<PreferenceModel>>

    @Query("select * from preference where projectId = (:id)")
    fun getPreferenceByIdLiveData(id: Int): LiveData<PreferenceModel>

    @Query("delete from preference")
    fun deletePreference()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPreference(preferenceModel: PreferenceModel)

    /**
     * 查询表中是否存在数据
     */
    @Query("select count(*) from preference")
    fun getPreferenceCountLiveData(): LiveData<Long>


    @Query("select * from preference order by created_time limit 1")
    fun getPreference(): PreferenceModel?

    @Query("select * from preference where projectId = (:id)")
    fun getPreferenceById(id: Int): List<PreferenceModel>

    @Query("select count(*) from preference")
    fun getPreferenceCount(): Long
}