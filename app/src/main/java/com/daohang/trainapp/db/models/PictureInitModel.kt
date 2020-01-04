package com.daohang.trainapp.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "picture_info")
data class PictureInitModel(
    @PrimaryKey
    //照片id
    val pictureId: ByteArray,
    //学员/教练编号
    val studentNumber: ByteArray,
    //上传模式
    val uploadMode: Byte = 1,
    //通道号
    val channelId: Byte = 0,
    //照片尺寸(249*320)
    val pictureSize: Byte = 0x01,
    //上传模式（学员登录/教练登录）
    val eventType: Byte = 5,
    //照片数据总包数
    val totalPackages: Short,
    //照片大小
    val imageSize: Int,
    //课堂id
    val classId: Int,
    //附加gnns数据包
    val gnnsData: ByteArray,
    //
    val faceConfidence: Byte = 100
): BaseModel() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PictureInitModel

        if (!pictureId.contentEquals(other.pictureId)) return false
        if (!studentNumber.contentEquals(other.studentNumber)) return false
        if (uploadMode != other.uploadMode) return false
        if (channelId != other.channelId) return false
        if (pictureSize != other.pictureSize) return false
        if (eventType != other.eventType) return false
        if (totalPackages != other.totalPackages) return false
        if (imageSize != other.imageSize) return false
        if (classId != other.classId) return false
        if (!gnnsData.contentEquals(other.gnnsData)) return false
        if (faceConfidence != other.faceConfidence) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pictureId.contentHashCode()
        result = 31 * result + studentNumber.contentHashCode()
        result = 31 * result + uploadMode
        result = 31 * result + channelId
        result = 31 * result + pictureSize
        result = 31 * result + eventType
        result = 31 * result + totalPackages
        result = 31 * result + imageSize
        result = 31 * result + classId
        result = 31 * result + gnnsData.contentHashCode()
        result = 31 * result + faceConfidence
        return result
    }
}