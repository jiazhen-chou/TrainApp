package com.daohang.trainapp.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "register")
data class RegisterModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    //平台编号
    var platformNumber: ByteArray,
    //驾校编号
    var schoolNumber: ByteArray,
    //终端编号
    var terminalNumber: ByteArray,
    //证书密钥
    var password: String,
    //证书
    var certification: ByteArray
){
    constructor(platformNumber: ByteArray,schoolNumber: ByteArray,terminalNumber: ByteArray,password: String,certification: ByteArray): this(0,platformNumber,schoolNumber,terminalNumber,password,certification)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RegisterModel

        if (id != other.id) return false
        if (!platformNumber.contentEquals(other.platformNumber)) return false
        if (!schoolNumber.contentEquals(other.schoolNumber)) return false
        if (!terminalNumber.contentEquals(other.terminalNumber)) return false
        if (password != other.password) return false
        if (!certification.contentEquals(other.certification)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + platformNumber.contentHashCode()
        result = 31 * result + schoolNumber.contentHashCode()
        result = 31 * result + terminalNumber.contentHashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + certification.contentHashCode()
        return result
    }

}