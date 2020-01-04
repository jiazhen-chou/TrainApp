package com.daohang.trainapp.utils

import com.daohang.trainapp.db.DaoHelper

fun sign(): ByteArray{
    val deviceId = DaoHelper.Preference.getPreference()?.vehiclePreference?.vehicleNumber
    val currentTime = System.currentTimeMillis() / 1000

    return byteArrayOf()
}