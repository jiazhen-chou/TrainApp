package com.daohang.trainapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.daohang.trainapp.db.models.RecordModel

@Dao
interface RecordDao {

    @Query("select * from record where record_number = (:recordNumber)")
    fun getRecord(recordNumber: String): RecordModel?

    @Query("select * from record where isBlindArea = 0")
    fun getBlindAreaRecords(): List<RecordModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecord(recordModel: RecordModel)

    @Query("delete from record")
    fun deleteAllRecords()
}