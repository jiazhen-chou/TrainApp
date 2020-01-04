package com.daohang.trainapp.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.daohang.trainapp.db.models.RegisterModel

@Dao
interface RegisterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(model: RegisterModel)

    @Query("select * from register limit 1")
    fun getRegisterInfo(): LiveData<RegisterModel>

    @Query("select count(*) from register")
    fun getCount(): LiveData<Long>

    @Query("delete from register")
    fun delete()
}