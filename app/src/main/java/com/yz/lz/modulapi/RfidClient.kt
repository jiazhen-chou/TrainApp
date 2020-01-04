package com.yz.lz.modulapi

interface RfidClient {

    fun openRfidDevice(): Boolean

    fun checkCard(): Boolean

    fun readCardType(): Byte

    fun readIdFromCard(): ByteArray

    fun readDataByAddrNum(addr: Short, keyType: Byte, key: ByteArray, len: Short): ByteArray

    fun writeDataByAddrNum(addr: Short, keyType: Byte, key: ByteArray, bytes: ByteArray): Boolean

    fun closeRfidDevice()


    fun setGPIO(pin: Int, type: Int): Boolean

    fun getGPIO(pin: Int): Int

    fun stringFromJNI(): String

    fun modifyPwd(addr: Short, keyType: Byte, originalKey: ByteArray, newKey: ByteArray): Boolean

    fun modifyControl(addr: Short, keyType: Byte, key: ByteArray, bytes: ByteArray): Boolean

    fun openFingerDevice(): Boolean

    fun checkPWD(pwd: String) : Boolean

    fun entryFingerprint(): Int

    fun entryAgainFingerprint(): Int

    fun matchFingerprint(): Boolean

    fun getFingerImage(path: String): Boolean

    fun downChar(): ByteArray

    fun upChar(bytes: ByteArray): Boolean

    fun genFingerTemp(): ByteArray

    fun reset()

    fun closeFingerDevice()
}