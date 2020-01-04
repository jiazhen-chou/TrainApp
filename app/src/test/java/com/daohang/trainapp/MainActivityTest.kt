package com.daohang.trainapp

import com.daohang.trainapp.services.decode
import com.daohang.trainapp.services.encode
import com.daohang.trainapp.services.sendClientMessage
import com.daohang.trainapp.utils.toHexString
import com.daohang.trainapp.utils.toInt2
import org.junit.Assert.assertArrayEquals
import org.junit.Test

class MainActivityTest {

    @Test
    fun testEncode(){
        val original = byteArrayOf(0x30.toByte(),0x7e.toByte(),0x08.toByte(),0x7d.toByte(),0x55.toByte())
        val result = byteArrayOf(0x30.toByte(),0x7d.toByte(),0x02.toByte(),0x08.toByte(),0x7d.toByte(),0x01.toByte(),0x55.toByte())
        assertArrayEquals(result, encode(original))
    }

    @Test
    fun testDecode(){
        val original = byteArrayOf(0x30.toByte(),0x7e.toByte(),0x02.toByte(),0x08.toByte(),0x7d.toByte(),0x55.toByte())
        val result = byteArrayOf(0x30.toByte(),0x7d.toByte(),0x02.toByte(),0x02.toByte(), 0x08.toByte(),0x7d.toByte(),0x01.toByte(),0x55.toByte())
        assertArrayEquals(original, decode(result))
    }

    @Test
    fun testSeperatePackage(){
        val message = sendClientMessage(0x0003, ByteArray(10))

        decode(message).run {
//            println(get(1).toString(16))
            val bodyParameter = slice(4 until 6)
//            println(bodyParameter[0])
//            println(bodyParameter[0].toString(2))
//            println(bodyParameter[1])
//            println(bodyParameter[1].toString(2))
//            assertEquals(bodyParameter[0].toString(2), '1')
//            println(bodyParameter[0].toBin())
            println(toHexString())
            println("序列号：${slice(14 until 16).toInt2()}")
        }
    }
}