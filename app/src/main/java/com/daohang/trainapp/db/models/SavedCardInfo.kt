package com.daohang.trainapp.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.daohang.trainapp.utils.enumCarType
import com.daohang.trainapp.utils.generateCountDown

@Entity(tableName = "saved_card_info")
data class SavedCardInfo(
    @PrimaryKey
    var id: Int,
    //类型，0-学员，1-教练
    var type: Int,
    //课程名(第二部分，第三部分)
    var className: String,
    //学员/教练编号
    var studentNumber: String,
    //学员/教练姓名
    var name: String,
    //学员/教练身份证号
    var identification: String,
    //驾校编号
    var company: String,
    //车辆类型
    var carType: Byte,
    //当前计时数
    var currentCount: Int = 0,
    //当前培训时长
    var currentTrainTime: Int = 0,
    //当前培训里程
    var currentTrainMiles: Double = 0.0,
    //已培训时长
    var trainTime: Int = 0,
    //已培训里程
    var trainMiles: Float = 0F
){
    override fun toString(): String {
        return "id：$id, ${if (type == 0) "学员" else "教练"}, 课程名：$className, 姓名：$name, 身份证号：$identification, 驾校编号：$company， 车辆类型: ${enumCarType(carType).name}, 当前培训时长: ${generateCountDown(currentTrainTime)}"
    }
}