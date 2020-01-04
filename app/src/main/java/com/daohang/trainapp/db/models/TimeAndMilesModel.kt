package com.daohang.trainapp.db.models

/**
 * 存储当前培训部分的里程和时长
 */
data class TimeAndMilesModel(
    //总学时
    var totalTime: Int,
    //当前部分已培训学时
    var currentTime: Int,
    //总里程
    var totalMiles: Int,
    //当前部分已培训里程
    var currentMiles: Int
){
    override fun toString(): String {
        return "总学时：$totalTime, 当前部分已培训学时: $currentTime, 总里程：$totalMiles, 当前部分已培训里程: $currentMiles"
    }
}