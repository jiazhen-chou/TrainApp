package com.daohang.trainapp.services

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.daohang.trainapp.constants.ProtocolReceive
import com.daohang.trainapp.constants.ProtocolSend
import com.daohang.trainapp.db.DaoHelper
import com.daohang.trainapp.db.models.RegisterModel
import com.daohang.trainapp.db.models.TimeAndMilesModel
import com.daohang.trainapp.rsa.Base64
import com.daohang.trainapp.utils.*

/*
    服务器返回消息
 */

const val CERTIFICATION_LENGTH = 4775
var receiveCertification = false
val certification: MutableList<Byte> = mutableListOf()

var timeAndMilesModel = MutableLiveData<TimeAndMilesModel>(TimeAndMilesModel(0, 0, 0, 0))

object ServerMessages {

    fun handleMessageReceived(message: ByteArray) {
        decode(message).run {
            val header = getMessageHeader(this)
            val body = getMessageBody(this)

            Log.d("ServerMessages", "接收消息：0x${toHexString().substring(4 until 8)}")

            if (receiveCertification) {
                saveCertification(this)
                return
            } else {
                when (toHexString().substring(4 until 8).toInt()) {
                    ProtocolReceive.RECEIVE_PENETRATE_MESSAGE -> handle8900Response(body)
                    ProtocolReceive.RECEIVE_SERVER_COMMON -> handleCommonResponse(body)
                    ProtocolReceive.RECEIVE_REGISTER -> {
//                        updateDatabase()
                        if (body[2] == 0.toByte()) {
                            println("注册成功")
                            sendTTSMsg("终端注册成功")
                            receiveCertification = true
                            certification.clear()
                            saveCertification(this)
                        } else {
                            when (body[2]) {
                                1.toByte() -> {
                                    println("车辆已被注册")
                                    sendTTSMsg("车辆已被注册")
                                }
                                2.toByte() -> {
                                    println("数据库中无该车辆")
                                    sendTTSMsg("数据库中无该车辆")
                                }
                                3.toByte() -> {
                                    println("终端已被注册")
                                    sendTTSMsg("终端已被注册")
                                    sendProtocolData(logout())
                                }
                                4.toByte() -> {
                                    println("数据库中无该终端")
                                    sendTTSMsg("数据库中无该终端")
                                }
                            }
                        }
                    }
                }
            }
            canSendNext = true
        }
    }

    /**
     * 获取消息头
     */
    private fun getMessageHeader(message: ByteArray): ByteArray {
        return if (isMultiPackage(message)) {
            message.sliceArray(1..20)
        } else {
            message.sliceArray(1..16)
        }
    }

    /**
     * 获取消息体
     */
    private fun getMessageBody(message: ByteArray): ByteArray {
        return if (isMultiPackage(message)) {
            //分包
            message.sliceArray(21 until message.size - 2)
        } else {
            //不分包
            message.sliceArray(17 until message.size - 2)
        }
    }

    /**
     * 获取分包总包数
     */
    private fun getPackageCount(header: ByteArray) = header.slice(16 until 18).toInt2()

    /**
     * 获取当前分包序号
     */
    private fun getPackageIndex(header: ByteArray) = header.slice(18 until 20).toInt2()

    /**
     * 是否分包
     */
    private fun isMultiPackage(message: ByteArray) = message[4].toBin()[2] == '1'

    /**
     * 处理下行透传消息
     */
    private fun handle8900Response(body: ByteArray) {
        println("收到下行透传消息, id为0x${body.sliceArray(1 until 3).toHexString()}")
        val contentLength = body.slice(25 until 27).toInt2()
        when (body.sliceArray(1 until 3).toHexInt()) {
            ProtocolReceive.RECEIVE_EXTEND_COACH_LOGIN ->
                println("教练登录")
            ProtocolReceive.RECEIVE_EXTEND_STUDENT_LOGIN -> {
                println("学员登录, 数据长度为：$contentLength")
                println("数据内容：${body.toHexString()}")
                if (contentLength > 26) {
                    val model = parseStudentLogin(body.sliceArray(27 until 27 + contentLength))
                    println(model.toString())
                    timeAndMilesModel.postValue(model)
                }
            }
            else -> Unit

        }
        updateDatabase()
    }

    /**
     * 解析学员登录信息
     */
    private fun parseStudentLogin(data: ByteArray) = TimeAndMilesModel(
        totalTime = data.sliceArray(17 until 19).toHexInt(),
        currentTime = data.sliceArray(19 until 21).toHexInt(),
        totalMiles = data.sliceArray(21 until 23).toHexInt(),
        currentMiles = data.sliceArray(23 until 25).toHexInt()
    )

    /**
     * 处理通用应答
     */
    private fun handleCommonResponse(body: ByteArray) {
        val result = body[4]
        when (val messageId = body.sliceArray(2 until 4).toHexInt()) {
            ProtocolSend.SEND_VALIDATION -> {
                if (result == 0.toByte()) {
                    println("鉴权成功")
                    sendTTSMsg("终端鉴权成功")
                    isOnline.postValue(true)
                } else {
                    sendProtocolData(register())
                }
            }
            ProtocolSend.SEND_LOGOUT -> {
                if (result == 0.toByte())
                    println("注销成功")
                sendProtocolData(register())
            }
            ProtocolSend.SEND_PENETRATE_MESSAGE -> {
                if (result == 0.toByte())
                    println("0900消息应答成功")
            }
            else -> println("收到通用应答: 0x${messageId.toByteArray2().toHexString()}")
        }
//        if (result == 0.toByte())
        updateDatabase()

        canSendNext = true
    }

    /**
     * 注册
     */
    private fun saveCertification(body: ByteArray) {
        certification.addAll(body.toList())

        if (certification.size == CERTIFICATION_LENGTH) {
            //继续发送消息
            println("证书保存成功，长度为: ${certification.size}")
            parseCertificationData()
            receiveCertification = false
        } else {
            println("证书长度错误")
        }
    }

    /**
     * 解析证书数据
     */
    private fun parseCertificationData() {
        val indexList = mutableListOf<Int>()
        val packageList = mutableListOf<List<Byte>>()
        val certificationData = mutableListOf<Byte>()

        for ((index, value) in certification.withIndex()) {
            if (value == 0x7e.toByte()) {
                indexList.add(index)
            }
        }

        if (indexList.size % 2 == 0) {
            for (index in indexList.indices step 2) {
                packageList.add(certification.slice(indexList[index]..indexList[index + 1]))
            }
        }

        for (value in packageList) {
            Log.d("解析证书", "证书数据：${value.toByteArray().toHexString()}")

            certificationData.addAll(getMessageBody(value.toByteArray()).toList())
        }
        saveCertificationToDb(certificationData.toByteArray())
    }

    /**
     * 证书保存至数据库
     */
    private fun saveCertificationToDb(body: ByteArray) {
        println(body.sliceArray(52 until body.size).toHexString())
        val registerModel = RegisterModel(
            body.sliceArray(3 until 8),
            body.sliceArray(8 until 24),
            body.sliceArray(24 until 40),
            MyBuffer(body.sliceArray(40 until 52)).getString(12),
            Base64.decode(String(body.sliceArray(52 until body.size)))
        )

        Log.d("保存证书", "解码后：${registerModel.certification.toHexString()}")

        DaoHelper.Register.delete()
        DaoHelper.Register.insert(registerModel)
        sendProtocolData(validate(registerModel))
    }

    /**
     * 鉴权
     */
    private fun handleValidation(body: ByteArray) {

    }

    private fun updateDatabase() = DaoHelper.Message.updateMessageSendState(currentModelId)
}