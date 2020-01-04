package com.daohang.trainapp.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message")
data class MessageModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    //消息序列号
    val sequenceNum: Int,
    //消息id
    val messageId: Int,
    //消息内容
    val messageContent: ByteArray
): BaseModel() {
    constructor(sequenceNum: Int, messageId: Int, messageContent: ByteArray): this(0,sequenceNum,messageId,messageContent){
//        Log.d("MessageModel","序列号为：$sequenceNum")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MessageModel

        if (!messageContent.contentEquals(other.messageContent)) return false

        return true
    }

    override fun hashCode(): Int {
        return messageContent.contentHashCode()
    }
}