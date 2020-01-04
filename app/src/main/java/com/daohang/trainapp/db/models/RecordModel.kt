package com.daohang.trainapp.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 学时记录表
 */

@Entity(tableName = "record")
data class RecordModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    //学时记录编号
    val record_number: ByteArray,
    //上报类型---0x01:自动上报， 0x02:应中心要求上报
    val record_type: Int,
    //学员编号
    val student_number: String,
    //教练员编号
    val coach_number: String,
    //课程id
    val class_id: Int,
    //课程编码
    val class_name: String,
    //记录状态---0:正常，1:异常
    val record_state: Int,
    //最大速度（1min内最大卫星速度，单位1/10km/h)
    val max_speed: Int,
    //里程（1min内行驶总里程）
    val miles: Int,
    //附加GNNS数据包（包括基本GNNS数据包+位置信息附加项中的里程和转速）
    val gnns_data: ByteArray

):BaseModel(){
    constructor(recordNumber: ByteArray, recordType: Int, studentNumber: String, coachNumber: String, classId: Int, className: String, recordState: Int, maxSpeed: Int, miles: Int, gnnsData: ByteArray):
            this(0, recordNumber, recordType, studentNumber, coachNumber, classId, className, recordState, maxSpeed, miles, gnnsData)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecordModel

        if (id != other.id) return false
        if (record_number != other.record_number) return false
        if (record_type != other.record_type) return false
        if (student_number != other.student_number) return false
        if (coach_number != other.coach_number) return false
        if (class_id != other.class_id) return false
        if (class_name != other.class_name) return false
        if (record_state != other.record_state) return false
        if (max_speed != other.max_speed) return false
        if (miles != other.miles) return false
        if (!gnns_data.contentEquals(other.gnns_data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + record_number.hashCode()
        result = 31 * result + record_type
        result = 31 * result + student_number.hashCode()
        result = 31 * result + coach_number.hashCode()
        result = 31 * result + class_id.hashCode()
        result = 31 * result + class_name.hashCode()
        result = 31 * result + record_state
        result = 31 * result + max_speed.hashCode()
        result = 31 * result + miles.hashCode()
        result = 31 * result + gnns_data.contentHashCode()
        return result
    }
}