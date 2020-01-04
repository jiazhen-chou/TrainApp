package com.daohang.trainapp.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.daohang.trainapp.constants.LOGIN

/**
 * 学员登入登出表
 */
@Entity(tableName = "student_state")
data class StudentStateModel(
    //课堂id
    val class_id: Int,
    //登入0/登出1
    val login_type: Int,
    //学员编号
    val student_number: String,
    //当前教练员编号
    val coach_number: String,
    //培训课程编码
    val class_name: String,
    //本次登录总时长
    val totalTime: Int,
    //本次登录总里程
    val totalMiles: Int,
    //GNNS数据包
    val gnns_data: ByteArray
):BaseModel(){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    override fun toString(): String {
        return "课堂id：$class_id,${if (login_type == LOGIN) "登录" else "登出"} ,学员编号：$student_number ,教练编号: $coach_number ,课程编码：$class_name ,登录总时长：$totalTime 分钟 ,登录总里程：$totalMiles km"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StudentStateModel

        if (class_id != other.class_id) return false
        if (login_type != other.login_type) return false
        if (student_number != other.student_number) return false
        if (coach_number != other.coach_number) return false
        if (class_name != other.class_name) return false
        if (totalTime != other.totalTime) return false
        if (totalMiles != other.totalMiles) return false
        if (!gnns_data.contentEquals(other.gnns_data)) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = class_id
        result = 31 * result + login_type
        result = 31 * result + student_number.hashCode()
        result = 31 * result + coach_number.hashCode()
        result = 31 * result + class_name.hashCode()
        result = 31 * result + totalTime
        result = 31 * result + totalMiles
        result = 31 * result + gnns_data.contentHashCode()
        result = 31 * result + id
        return result
    }
}