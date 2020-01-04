package com.daohang.trainapp.utils

import android.util.Log
import com.daohang.trainapp.MyApplication
import com.yz.lz.modulapi.*
import java.nio.charset.Charset

var PASSWORD = "h*V5t&"

/**
 * 枚举车辆类型
 */
fun enumCarType(byte: Byte): CarType {
    return when (byte.toInt()) {
        0x10 -> CarType.A0
        0x11 -> CarType.A1
        0x12 -> CarType.A2
        0x13 -> CarType.A3
        0x20 -> CarType.B0
        0x21 -> CarType.B1
        0x22 -> CarType.B2
        0x30 -> CarType.C0
        0x31 -> CarType.C1
        0x32 -> CarType.C2
        0x33 -> CarType.C3
        0x34 -> CarType.C4
        0x35 -> CarType.C5
        0x40 -> CarType.D
        0x50 -> CarType.E
        0x60 -> CarType.F
        0x70 -> CarType.M
        else -> CarType.None
    }
}

fun enumCarType(byte: Int): CarType {
    return when (byte) {
        10 -> CarType.A0
        11 -> CarType.A1
        12 -> CarType.A2
        13 -> CarType.A3
        20 -> CarType.B0
        21 -> CarType.B1
        22 -> CarType.B2
        30 -> CarType.C0
        31 -> CarType.C1
        32 -> CarType.C2
        33 -> CarType.C3
        34 -> CarType.C4
        35 -> CarType.C5
        40 -> CarType.D
        50 -> CarType.E
        60 -> CarType.F
        70 -> CarType.M
        else -> CarType.None
    }
}

/**
 * 枚举卡类型
 */
fun enumCardType(byte: Byte): CardType {
    return when (byte.toInt()) {
        1 -> CardType.StudentCard
        2 -> CardType.TheoryCoachCard
        3 -> CardType.PracticeCoachCard
        4 -> CardType.CommonCoachCard
        5 -> CardType.DaohangManagerCard
        6 -> CardType.DrivingSchoolManagerCard
        else -> CardType.UnKnown
    }
}

fun readCard(addr: Short, len: Short): ByteArray {
    val result =
        MyApplication.rfidInstance.readDataByAddrNum(addr, 0, PASSWORD.toByteArray(), len)
    return if (result != null && result.isNotEmpty())
        result
    else
        byteArrayOf()
}

fun readCardInfo(): CardInfo? {
    readCard(32, 48 * 3).run {
        if (this.isNotEmpty()) {
            val cardInfo = CardInfo()

            cardInfo.cardType = enumCardType(this[4])
            cardInfo.id = this.slice(5 until 21).toString(CHARSET_UTF8)
            cardInfo.name = this.slice(21 until 41).toString(CHARSET_GB)
            cardInfo.practiceTimePerDay = this.sliceArray(41 until 45).toHexString().toInt(16)
            cardInfo.identification = this.slice(48 until 66).toString(CHARSET_UTF8)
            cardInfo.company = this.slice(66 until 86).toString(CHARSET_GB)
            cardInfo.validityPeriod = "20${this.slice(86 until 89).bcd2Str()}"
            cardInfo.carType = enumCarType(this[89])
            cardInfo.subjectOneTotalTime = this.sliceArray(96 until 100).toHexString().toInt(16)
            cardInfo.subjectTwoTotalTime = this.sliceArray(100 until 104).toHexString().toInt(16)
            cardInfo.subjectTwoTotalMiles = this.sliceArray(104 until 108).toHexString().toInt(16)
            cardInfo.subjectThreeTotalTime = this.sliceArray(108 until 112).toHexString().toInt(16)
            cardInfo.subjectThreeTotalMiles = this.sliceArray(112 until 116).toHexString().toInt(16)
            cardInfo.subjectFourTotalMiles = this.sliceArray(116 until 120).toHexString().toInt(16)

            readCard(1232, 48).run {
                if (this.isNotEmpty()) {
                    cardInfo.cardState = this[0]
                    cardInfo.practiceTime = this.sliceArray(1 until 5).toHexString().toInt(16)
                    cardInfo.checkInTimes = this.slice(5 until 7).toShort()
                    cardInfo.subjectOneLearnedTime = this.sliceArray(7 until 11).toHexString().toInt(16)
                    cardInfo.subjectTwoLearnedTime = this.sliceArray(11 until 15).toHexString().toInt(16)
                    cardInfo.subjectTwoLearnedMiles = this.sliceArray(15 until 19).toHexString().toInt(16)
                    cardInfo.subjectThreeLearnedTime = this.sliceArray(19 until 23).toHexString().toInt(16)
                    cardInfo.subjectThreeLearnedMiles = this.sliceArray(23 until 27).toHexString().toInt(16)
                    cardInfo.subjectFourLearnedTime = this.sliceArray(27 until 31).toHexString().toInt(16)
                    cardInfo.lastExitDate = this.slice(35 until 38).bcd2Str()

                    Log.d("RfidFuncs", cardInfo.toString())

                    return cardInfo
                }
            }
        }
    }
    return null
}

fun writeSubjectTwo(data: ByteArray) = writeCard(1243, data)

fun writeSubjectThree(data: ByteArray) = writeCard(1251, data)

fun writeLastExitDate(data: ByteArray) = writeCard(1267, data)

fun writeToadyPraticeTime(data: ByteArray) = writeCard(1233, data)

fun writeCard(addr: Short, data: ByteArray): Boolean =
    MyApplication.rfidInstance.writeDataByAddrNum(addr, 0, PASSWORD.toByteArray(), data)

fun readCardId() = MyApplication.rfidInstance.readIdFromCard()

fun checkCard() = MyApplication.rfidInstance.checkCard()