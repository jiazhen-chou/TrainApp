package com.daohang.trainapp.db.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * 位置信息表
 */
@Entity(tableName = "location")
data class LocationModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    //报警标识
    val alarm_state: Int,
    //状态
    val state: Int,
    //纬度
    val latitude: Double,
    //经度
    val longitude: Double,
    //行驶记录速度
    val record_speed: Float,
    //卫星定位速度
    val satellite_speed: Float,
    //方向
    val direction: Int,
    //车辆里程表读数
    var obd_miles: Int,
    //车辆油表读数
    var obd_fuel: Int,
    //海拔高度
    var altitude: Int,
    //发动机转速
    var rotate_speed: Int
): BaseModel(){
    @Ignore
    constructor(alarm_state: Int = 0, state: Int = 0, latitude: Double = 0.0, longitude: Double = 0.0,
                record_speed: Float = 0F, satellite_speed: Float = 0F, direction: Int = 0):
            this(0,alarm_state, state, latitude, longitude, record_speed, satellite_speed, direction,0,0,0,0)
}