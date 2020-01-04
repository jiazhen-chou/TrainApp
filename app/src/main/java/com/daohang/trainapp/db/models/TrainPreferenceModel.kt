package com.daohang.trainapp.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "trainPreference")
data class TrainPreferenceModel(
    @PrimaryKey(autoGenerate = true) val id: Int,

    //拍照间隔
    @SerializedName("photo_time_interval")
    val picture_interval: Int = 0,

    //最高时速
    @SerializedName("overspeed_threshol")
    val max_speed: Int,

    //单日最高培训时长
    @SerializedName("max_class_hour")
    val max_hour: Int,

    //是否开启围栏
    @SerializedName("enclosure")
    val use_fence: Int,

    //是否开启obd
    @SerializedName("obd")
    val use_obd: Int,

    //准教车型验证
    @SerializedName("coach_teach_permitted")
    val check_car_type: String
)