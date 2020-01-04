package com.daohang.trainapp.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.daohang.trainapp.db.models.MessageModel
import retrofit2.http.DELETE

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(messageModel: MessageModel)

    @Query("select count(*) from message")
    fun getCount(): LiveData<Long>

    @Query("select * from message where hasUploaded=0 order by time limit 1")
    fun getLatestMessage(): MessageModel?

    @Query("select * from message where id=:id")
    fun getMessageById(id: Int): MessageModel?

    @Query("select * from message")
    fun getAllMessage(): List<MessageModel>

    @Query("update message set hasUploaded=1 where id=:id")
    fun updateMessageSendState(id: Int)

    @Query("update message set hasUploaded=1 where sequenceNum=:sequenceNumber")
    fun updateMessageSendStateBySequence(sequenceNumber: Int)

    @Query("delete from message")
    fun delete()
}