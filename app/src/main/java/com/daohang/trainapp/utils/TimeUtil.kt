package com.daohang.trainapp.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * 获取当前时间(分钟)
 */
fun currentTimeWithMinutes(): String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINESE).format(
    Date()
)

/**
 * 获取当前时间(秒)
 */
fun currentTimeWithSeconds(): String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE).format(
    Date()
)

/**
 * 格式化时间
 * @param time 时间戳
 */
fun formatTime(time: Long) : String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINESE).format(
    Date(time)
)

fun formatTimeWithSeconds(time: Long) : String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE).format(
    Date(time)
)

fun formatTimeHms(time: Long): String = SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINESE).format(Date(time))

/**
 * 学员登录的时间
 */
fun formatLogoutTime(time: Long): String = SimpleDateFormat("yyMMddHHmmss", Locale.CHINESE).format(Date(time))

fun formatDate(time: Long): String = SimpleDateFormat("yyMMdd", Locale.CHINESE).format(Date(time))

fun getDateTime(): ByteArray{
    val dateTime = ByteArray(6)
    val time = IntArray(6)
    val c = Calendar.getInstance()
    time[0] = c[Calendar.YEAR] - 2000
    time[1] = c[Calendar.MONTH] + 1
    time[2] = c[Calendar.DATE]
    time[3] = c[Calendar.HOUR_OF_DAY]
    time[4] = c[Calendar.MINUTE]
    time[5] = c[Calendar.SECOND]
    for (i in 0..5) {
        dateTime[i] = intToHex(time[i])
    }
    return dateTime
}

fun intToHex(time: Int): Byte {
    return ((time / 10 shl 4) + time % 10).toByte()
}


/**
 * 时间戳转换为时分秒格式(计时)
 */
fun generateCountDown(time: Int): String{

    val buffer = StringBuffer()
    val hour: Int = time / 3600
    val minute: Int = (time % 3600)/60
    val second: Int = time % 60
    if (hour < 10){
        buffer.append("0${hour}")
    } else {
        buffer.append(hour)
    }

    buffer.append(":")

    if (minute < 10){
        buffer.append("0${minute}")
    } else{
        buffer.append(minute)
    }

    buffer.append(":")

    if (second < 10){
        buffer.append("0${second}")
    } else{
        buffer.append(second)
    }

    return buffer.toString()
}