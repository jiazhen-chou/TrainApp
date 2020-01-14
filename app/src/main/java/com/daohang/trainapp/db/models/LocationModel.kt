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
    var alarm_state: Int,
    //状态
    var state: Int,
    //纬度
    var latitude: Double,
    //经度
    var longitude: Double,
    //行驶记录速度
    var record_speed: Float,
    //卫星定位速度
    var satellite_speed: Float,
    //方向
    var direction: Int,
    //车辆里程表读数
    var obd_miles: Float,
    //车辆油表读数
    var obd_fuel: Float,
    //海拔高度
    var altitude: Int,
    //发动机转速
    var rotate_speed: Int
): BaseModel(){
    @Ignore
    constructor(alarm_state: Int = 0, state: Int = 0, latitude: Double = 0.0, longitude: Double = 0.0,
                record_speed: Float = 0F, satellite_speed: Float = 0F, direction: Int = 0):
            this(0,alarm_state, state, latitude, longitude, record_speed, satellite_speed, direction,0f,0f,0,0)
}