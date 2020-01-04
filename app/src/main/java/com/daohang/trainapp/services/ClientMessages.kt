package com.daohang.trainapp.services

import com.daohang.trainapp.utils.toBcd
import com.daohang.trainapp.utils.toByteArray2
import java.math.BigInteger
import kotlin.experimental.xor

/*
    客户端发送消息
 */

/**
 * 发送消息
 * @param command 消息id
 * @param body 消息体
 */
fun sendClientMessage(command: Int, body: ByteArray): ByteArray {
    val data = encode(calculateCheckCode(addMessageHeader(command, body, 1, 1)))
    val message = ByteArray(data.size + 2)
    message[0] = 0x7e.toByte()
    message[message.size - 1] = 0x7e.toByte()
    System.arraycopy(data, 0, message, 1, data.size)

    return message
}

/**
 * 发送消息（分包）
 * @param command 消息id
 * @param body 消息体
 * @param totalPackages 分包总包数
 * @param currentIndex 当前包序号
 */
fun sendClientMessage(
    command: Int,
    body: ByteArray,
    totalPackages: Int,
    currentIndex: Int
): ByteArray {
    val data =
        encode(calculateCheckCode(addMessageHeader(command, body, totalPackages, currentIndex)))
    val message = ByteArray(data.size + 2)
    message[0] = 0x7e.toByte()
    message[message.size - 1] = 0x7e.toByte()
    System.arraycopy(data, 0, message, 1, data.size)

    //TODO 发送消息(先转义)
    return message
}

/**
 * @param bodyAndHeader 添加了消息头的消息
 * @return 返回添加了校验码的消息
 */
private fun calculateCheckCode(bodyAndHeader: ByteArray): ByteArray {
    var checkCode = bodyAndHeader[0]
    for (index in 0..bodyAndHeader.size - 2)
        checkCode = checkCode xor bodyAndHeader[index + 1]

    val tempList = bodyAndHeader.toMutableList()
    tempList.add(checkCode)
    return tempList.toByteArray()
}

/**
 * @param command 消息指令
 * @param body 消息体
 * @param totalPackages 分包总包数
 * @param currentIndex 当前包序号
 * @return 添加了消息头的消息
 */
private fun addMessageHeader(
    command: Int,
    body: ByteArray,
    totalPackages: Int,
    currentIndex: Int
): ByteArray {
    val temp = mutableListOf<Byte>()
    //协议版本号
    temp.add(0x80.toByte())
    //消息Id
    temp.addAll(command.toByteArray2().toList())
    //消息体属性,共2字节，16位
    temp.addAll(generateBodyParameter(body.size, totalPackages > 1).toList())

    //终端手机号
//    preference?.let {
    temp.addAll("00000${globalPreferenceModel.vehiclePreference.clientId}".toBcd().toList())
//    }
//    temp.addAll("0000013012341111".toBcd().toList())
    //消息流水号
    temp.addAll(sequenceNumber.toByteArray2().toList())
    //预留
    temp.add(0x3c.toByte())
    //消息包封装项
    if (totalPackages > 1) {
        temp.addAll(totalPackages.toByteArray2().toList())
        temp.addAll(currentIndex.toByteArray2().toList())
    }

    println("分包----总包数$totalPackages,当前包序号$currentIndex")

    //放入消息体
    temp.addAll(body.toList())
    return temp.toByteArray()
}

/**
 * 消息体属性
 * @param length 消息体长度
 * @param separatePackage 是否分包
 */
private fun generateBodyParameter(length: Int, separatePackage: Boolean): ByteArray {
    val result = StringBuffer()
    result.append("00")
    result.append(if (separatePackage) "1" else "0")
    result.append("000")
    val lengthStr = length.toString(2)
    for (i in 0..9 - lengthStr.length)
        result.append("0")
    result.append(lengthStr)

    return BigInteger(result.toString(), 2).toInt().toByteArray2()
}