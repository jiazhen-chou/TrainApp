package com.daohang.trainapp.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.daohang.trainapp.db.models.LocationModel

@Dao
interface LocationDao {

    @Query("select * from location order by time")
    fun getLocations(): LiveData<List<LocationModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(locationModel: LocationModel)

    @Query("delete from location")
    fun deleteAll()
}