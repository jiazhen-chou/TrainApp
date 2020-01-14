package com.daohang.trainapp.services

import android.util.Log
import com.daohang.trainapp.MyApplication
import com.daohang.trainapp.constants.*
import com.daohang.trainapp.db.DaoHelper
import com.daohang.trainapp.db.models.*
import com.daohang.trainapp.rsa.HexBin
import com.daohang.trainapp.rsa.Sign
import com.daohang.trainapp.ui.registerInfo
import com.daohang.trainapp.utils.*
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.security.KeyStore
import java.security.PrivateKey

lateinit var globalPreferenceModel: PreferenceModel

fun insertMessage(model: MessageModel) {
    DaoHelper.Message.insertMessage(model)
    sequenceNumber++
    if ((sequenceNumber and 0xFFFF) == 0)
        sequenceNumber++
}

/**
 * 注册
 */
fun register(): ByteArray {
    val data = mutableListOf<Byte>()
    //省域id
    data.addAll(ProvinceId.toByteArray2().toList())
    //市县域id
    data.addAll(CityId.toByteArray2().toList())
    //制造商id
    data.addAll(ManufactureId.toByteArray().toList())
    //终端型号
    data.addAll(getDeviceType())
    //计时终端出厂序列号
    data.addAll(DeviceSerial.toByteArray().toList())
    //IMEI
    data.addAll(MyApplication.IMEI.toByteArray().toList())
    //车牌颜色
    data.add(CarColor.toByte())
    //车牌号
//    preference?.let {
    data.addAll(globalPreferenceModel.vehiclePreference.vehicleNumber.toByteArray(charset = Charset.forName("GBK")).toList())
//    }

    return sendClientMessage(ProtocolSend.SEND_REGISTER, data.toByteArray())
}

/**
 * 拼接终端型号
 */
private fun getDeviceType(): List<Byte> {
    val deviceType = mutableListOf<Byte>()
    deviceType.addAll(DeviceType.toByteArray().toList())
    if (deviceType.size < 20)
        deviceType.addAll(ByteArray(20 - deviceType.size).toList())
    return deviceType
}

/**
 * 鉴权
 */
fun validate(model: RegisterModel): ByteArray {
    val time: Long = System.currentTimeMillis() / 1000
    val data = mutableListOf<Byte>()
    data.addAll(time.toByteArray().toList())
    data.addAll(signCertification(model, time).toList())
    return sendClientMessage(ProtocolSend.SEND_VALIDATION, data.toByteArray())
}

/**
 * 证书加密
 */
fun signCertification(model: RegisterModel, time: Long): ByteArray {
    model.run {
        val keyStore = KeyStore.getInstance("PKCS12")
        keyStore.load(
            ByteArrayInputStream(model.certification) as InputStream,
            password.toCharArray()
        )
        val privateKey =
            keyStore.getKey(keyStore.aliases().nextElement(), password.toCharArray()) as PrivateKey
        return HexBin.decode(Sign().sign(terminalNumber, time, privateKey))
    }
}

/**
 * 证书签名
 */
fun signData(data: ByteArray, model: RegisterModel): ByteArray {
    model.run {
        val keyStore = KeyStore.getInstance("PKCS12")
        keyStore.load(
            ByteArrayInputStream(certification) as InputStream,
            password.toCharArray()
        )
        val privateKey =
            keyStore.getKey(keyStore.aliases().nextElement(), password.toCharArray()) as PrivateKey
        return HexBin.decode(Sign().sign(data, 0, privateKey))
    }
}

/**
 * 位置信息
 */
fun locate() = sendClientMessage(ProtocolSend.SEND_LOCATION, gnnsMessage())

/**
 * 基本位置信息
 */
fun gnnsMessage(): ByteArray {
    currentLocation?.run {
        val data = mutableListOf<Byte>()
        //报警标识
//        data.addAll(alarm_state.toByteArray4().toList())
        val warningFlag =
            if (isTraining) WarningFlag.getWarningFlag() else WarningFlag.getNoWarningFlag()
        data.addAll(warningFlag.toList())
        //状态
        data.addAll(state.toByteArray4().toList())
        //纬度
        data.addAll((latitude * 1000000).toLong().toByteArray().toList())
        //经度
        data.addAll((longitude * 1000000).toLong().toByteArray().toList())
        //行驶记录速度
        data.addAll((record_speed * 10).toInt().toByteArray2().toList())
        //卫星定位速度
        data.addAll((satellite_speed * 10).toInt().toByteArray2().toList())
        //方向
        data.addAll(direction.toByteArray2().toList())
        //时间
        data.addAll(getDateTime().toList())
        //位置附加信息
        data.addAll(locationAdditional().toList())
        return data.toByteArray()
    }
    return ByteArray(28)
}

/**
 * 分钟学时位置附加信息
 */
fun recordGnnsAddition(): ByteArray {
    currentLocation?.run {
        val data = mutableListOf<Byte>()
        data.addAll(gnnsMessage().toList())
        //车辆里程表读数
        data.addAll(byteArrayOf(0x01.toByte(), 4.toByte()).toList())
        data.addAll((obd_miles * 10).toInt().toByteArray4().toList())
        //发动机转速
        data.addAll(byteArrayOf(0x04.toByte(), 2.toByte()).toList())
        data.addAll(rotate_speed.toByteArray2().toList())

        return data.toByteArray()
    }
    return ByteArray(38)
}

/**
 * 位置附加信息
 */
fun locationAdditional(): ByteArray {
    currentLocation?.run {
        val data = mutableListOf<Byte>()
        //车辆里程表读数
        data.addAll(byteArrayOf(0x01.toByte(), 4.toByte()).toList())
        data.addAll((obd_miles * 10).toInt().toByteArray4().toList())
        //车辆油表读数
        data.addAll(byteArrayOf(0x02.toByte(), 2.toByte()).toList())
        data.addAll((obd_fuel * 10).toInt().toByteArray2().toList())
        //海拔高度
        data.addAll(byteArrayOf(0x03.toByte(), 2.toByte()).toList())
        data.addAll(altitude.toByteArray2().toList())
        //发动机转速
        data.addAll(byteArrayOf(0x04.toByte(), 2.toByte()).toList())
        data.addAll(rotate_speed.toByteArray2().toList())
    }
    return byteArrayOf()
}

/**
 * 注销
 */
fun logout(): ByteArray = sendClientMessage(0x0003, byteArrayOf())

/**
 * 扩展计时培训消息
 * @param command 消息id
 * @param content 消息内容
 */
fun extenededProtocol(command: Int, content: ByteArray): ByteArray {
    registerInfo?.run {
        val data = mutableListOf<Byte>()
        //透传消息类型，驾培业务为0x13
        data.add(0x13)
        //消息id
        data.addAll(command.toByteArray2().toList())
        //bit0表示消息时效类型，应答中也应附带此内容，0：实时消息，1：补传消息；
        //bit1表示应答属性，0：不需要应答，1：需要应答；
        //bit4-7表示加密算法，0：未加密，1：SHA1，2：SHA256；
        //其他保留
        var msgProperty = 0
        //是否为盲区（判定条件为）
        if (!isOnline.value!!)
            msgProperty = msgProperty or 0x0001
        //是否需要应答
        msgProperty = msgProperty or 0x0002
        //是否加密
        msgProperty = msgProperty or 0x0020
        data.addAll(msgProperty.toByteArray2().toList())
        //驾培包序号
        extentedSequenceNumber++
        if ((extentedSequenceNumber and 0xFFFF) == 0)
            extentedSequenceNumber++
        data.addAll(extentedSequenceNumber.toByteArray2().toList())
        //计时终端编号
        data.addAll(terminalNumber.toList())
        //消息长度
        data.addAll(content.size.toByteArray4().toList())
        //消息内容
        data.addAll(content.toList())
        //校验串
        data.addAll(signData(data.subList(1, data.size).toByteArray(), this).toList())

        return data.toByteArray()

    }
    return byteArrayOf()
}


/**
 * 上传教练登录
 * @param model 教练信息
 */
fun coachLogin(model: CoachStateModel): ByteArray = mutableListOf<Byte>().run {
    val data = mutableListOf<Byte>()
    data.addAll(model.coach_number.toByteArray().toList())
    data.addAll(model.coach_identification.toAscii().toList())
    data.addAll(model.car_type.toByteArray().toList())
    data.addAll(gnnsMessage().toList())

    return sendClientMessage(
        ProtocolSend.SEND_PENETRATE_MESSAGE,
        extenededProtocol(ProtocolSend.SEND_COACH_LOGIN, data.toByteArray())
    )
}
//        : ByteArray {

//}

/**
 * 教练登出
 * @param coachNumber 教练编号
 */
fun coachLogout(coachNumber: String): ByteArray {
    val data = mutableListOf<Byte>()
    data.addAll(coachNumber.toByteArray().toList())
    data.addAll(gnnsMessage().toList())
    return sendClientMessage(
        ProtocolSend.SEND_PENETRATE_MESSAGE,
        extenededProtocol(ProtocolSend.SEND_COACH_LOGOUT, data.toByteArray())
    )
}

/**
 * 学员登录
 * @param model 学员信息
 */
fun studentLogin(model: StudentStateModel): ByteArray {
    val data = mutableListOf<Byte>()
    data.addAll(model.student_number.toByteArray().toList())
    data.addAll(model.coach_number.toByteArray().toList())
    data.addAll(model.class_name.toBcd().toList())
    data.addAll(model.class_id.toByteArray4().toList())
    data.addAll(gnnsMessage().toList())

    Log.e("学员登录", "classId: ${model.class_id}")
    return sendClientMessage(
        ProtocolSend.SEND_PENETRATE_MESSAGE,
        extenededProtocol(ProtocolSend.SEND_STU_LOGIN, data.toByteArray())
    )
}

/**
 * 学员登出
 * @param model 学员信息
 */
fun studentLogout(model: StudentStateModel): ByteArray {
    val data = mutableListOf<Byte>()
    data.addAll(model.student_number.toByteArray().toList())
    data.addAll(formatLogoutTime(model.time).toBcd().toList())
    data.addAll(model.totalTime.toByteArray2().toList())
    data.addAll(model.totalMiles.toByteArray2().toList())
    data.addAll(model.class_id.toByteArray4().toList())
    data.addAll(gnnsMessage().toList())

    Log.e("学员登出", "classId: ${model.class_id}")
    return sendClientMessage(
        ProtocolSend.SEND_PENETRATE_MESSAGE,
        extenededProtocol(ProtocolSend.SEND_STU_LOGOUT, data.toByteArray())
    )
}

/**
 * 分钟学时
 */
fun minuteRecord(model: RecordModel): ByteArray {
    val data = mutableListOf<Byte>()
    //学时记录编号
    data.addAll(model.record_number.toList())
    //上报类型 0x01 自动上报  0x02 应中心要求上报
    data.add(0x01)
    //学员编号
    data.addAll(model.student_number.toByteArray().toList())
    //教练编号
    data.addAll(model.coach_number.toByteArray().toList())
    //课堂id
    data.addAll(model.class_id.toByteArray4().toList())
    //记录产生时间(HHmmss)
    data.addAll(formatTimeHms(model.time).substring(8 until 14).toBcd().toList())
    //培训课程
    data.addAll(model.class_name.toBcd().toList())
    //记录状态
    data.add(0x00)
    //最大速度
    data.addAll(model.max_speed.toByteArray2().toList())
    //里程
    data.addAll(model.miles.toByteArray2().toList())
    //基本位置信息+附加位置信息里的里程和转速
    data.addAll(model.gnns_data.toList())

    return sendClientMessage(
        ProtocolSend.SEND_PENETRATE_MESSAGE,
        extenededProtocol(ProtocolSend.SEND_MINUTE_RECORD, data.toByteArray())
    )
}

/**
 * 上传照片初始化
 */
fun initPicture(model: PictureInitModel): ByteArray {
    val data = mutableListOf<Byte>()
    //照片编号
    data.addAll(model.pictureId.toList())
    //学员编号
    data.addAll(model.studentNumber.toList())
    //上传模式
    data.add(model.uploadMode)
    //摄像头通道号
    data.add(model.channelId)
    //图片尺寸
    data.add(model.pictureSize)
    //发起图片的事件类型
    data.add(model.eventType)
    //总包数
    data.addAll(model.totalPackages.toByteArray().toList())
    //照片数据大小
    data.addAll(model.imageSize.toByteArray4().toList())
    //课堂id
    data.addAll(model.classId.toByteArray4().toList())
    //附加gnns数据包
    data.addAll(model.gnnsData.toList())
    //人脸识别置信度
    data.add(model.faceConfidence)

    return sendClientMessage(
        ProtocolSend.SEND_PENETRATE_MESSAGE,
        extenededProtocol(ProtocolSend.SEND_PICTURE_INIT, data.toByteArray())
    )
}

/**
 * 照片分包
 */
fun separatePhoto(data: ByteArray, totalPackages: Int, currentIndex: Int) = sendClientMessage(
    ProtocolSend.SEND_PENETRATE_MESSAGE,
    data,
    totalPackages,
    currentIndex
)