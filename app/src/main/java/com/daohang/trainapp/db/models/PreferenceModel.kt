package com.daohang.trainapp.db.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 应用配置表
 */
@Entity(tableName = "preference")
data class PreferenceModel(
//    //主键id，自增
//    @PrimaryKey(autoGenerate = true)
//    val id: Int,
    //项目编号
    @PrimaryKey
    @SerializedName("projectId")
    val projectId: Int,
    //项目名称
    @SerializedName("name")
    val projectName: String,
    //api地址
    @SerializedName("platformIp")
    val apiDomain: String,
    //api端口
    @SerializedName("platformPort")
    val apiPort: Int,
    //websocket地址
    @SerializedName("onlineIp")
    val webSocketDomain: String,
    //websocket端口
    @SerializedName("onlinePort")
    val webSocketPort: Int,
    //认证方式：1.卡，2.卡+人脸识别,3.人脸识别
    @SerializedName("authType")
    val authType: Int,
    //A密码
    @SerializedName("passwordA")
    val passwordA: String,
    //B密码
    @SerializedName("passwordB")
    val passwordB: String,
    //升级code
    @SerializedName("code")
    val code: String,
    //车辆信息
    @Embedded
    var vehiclePreference: VehiclePreferenceModel,
    //创建时间
    val created_time: Long
): Serializable