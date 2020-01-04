package com.daohang.trainapp.db.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


/**
 * 车辆信息表
 */
@Entity(tableName = "vehicle_preference")
data class VehiclePreferenceModel(
    //终端id
    @PrimaryKey
    var clientId: String,
    //车型
    var vehicleType: String,
    //车牌号
    var vehicleNumber: String,
    //省
    var province: String,
    //市
    var city: String,
    //车牌颜色
    var vehicleColor: String
){
    @Ignore
    constructor(): this("","","","","", "")
}