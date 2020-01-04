package com.daohang.trainapp.services

/**
 * 消息转义（编码）
 * 0x7e => 0x7d + 0x02
 * 0x7d => 0x7d + 0x01
 * @param data 原始数据，包括消息头+消息体+校验码（来自客户端）
 */
fun encode(data: ByteArray): ByteArray{
    if (data.isEmpty())
        return byteArrayOf()

    val originalSize = data.size
    var addedLength = 0

    addedLength += data.filter { it == 0x7e.toByte() || it == 0x7d.toByte() }.size

    val transferedData = ByteArray(addedLength + originalSize)
    var index = 0

    for (b in data){
        when(b){
            0x7e.toByte() -> {
                transferedData[index++] = 0x7d.toByte()
                transferedData[index++] = 0x02.toByte()
            }
            0x7d.toByte() -> {
                transferedData[index++] = 0x7d.toByte()
                transferedData[index++] = 0x01.toByte()
            }
            else -> transferedData[index++] = b
        }
    }
    return transferedData
}

/**
 * 消息转义（解码）
 * 0x7d + 0x02 => 0x7e
 * 0x7d + 0x01 => 0x7d
 * @param data 原始数据，包括消息头+消息体+校验码（来自服务端）
 */
fun decode(data: ByteArray): ByteArray{
    if (data.isEmpty())
        return byteArrayOf()

    if (data.size < 2)
        return data

    val result = mutableListOf<Byte>()

    var shouldTransfer = false

    for (b in data){
        if (b == 0x7d.toByte()) {
            shouldTransfer = true
            continue
        }

        if (shouldTransfer && b == 0x01.toByte())
            result.add(0x7d.toByte())
        else if (shouldTransfer && b == 0x02.toByte())
            result.add(0x7e.toByte())
        else
            result.add(b)

        shouldTransfer = false
    }

    return result.toByteArray()
}