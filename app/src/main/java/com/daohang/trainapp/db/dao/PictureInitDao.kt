package com.daohang.trainapp.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.daohang.trainapp.db.models.PictureInitModel

@Dao
interface PictureInitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(model: PictureInitModel)

    @Query("select * from picture_info where hasUploaded=0 limit 1")
    fun getLatestPictureInfoLiveData(): LiveData<PictureInitModel>

    @Query("update picture_info set hasUploaded=1 where pictureId=:pictureId")
    fun updateStatus(pictureId: ByteArray)

    @Query("select * from picture_info where hasUploaded=0 limit 1")
    fun getLatestPictureInfo(): PictureInitModel?

    @Query("select * from picture_info where pictureId=:pictureId")
    fun getPictureInfoById(pictureId: ByteArray): PictureInitModel?
}