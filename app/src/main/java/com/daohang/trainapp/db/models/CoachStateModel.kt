package com.daohang.trainapp.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.daohang.trainapp.constants.LOGIN

/**
 * 教练员登入登出表
 */
@Entity(tableName = "coach_state")
data class CoachStateModel(
    //教练员编号
    val coach_number: String,
    //教练员身份证号
    val coach_identification: String,
    //教练员准教车型
    val car_type: String,
    //登录状态--0：登录，1：登出
    val login_type: Int,
    //附加gnns数据包
    val gnns_data: ByteArray
):BaseModel(){
    @PrimaryKey(autoGenerate = true) var id: Int = 0

    override fun toString(): String {
        return "教练员编号：$coach_number, 教练员身份证号：$coach_identification, 准教车型: $car_type, ${if (login_type == LOGIN) "登录" else "登出"}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CoachStateModel

        if (coach_number != other.coach_number) return false
        if (coach_identification != other.coach_identification) return false
        if (car_type != other.car_type) return false
        if (login_type != other.login_type) return false
        if (!gnns_data.contentEquals(other.gnns_data)) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = coach_number.hashCode()
        result = 31 * result + coach_identification.hashCode()
        result = 31 * result + car_type.hashCode()
        result = 31 * result + login_type
        result = 31 * result + gnns_data.contentHashCode()
        result = 31 * result + id
        return result
    }
}