package com.daohang.trainapp.utils

import java.util.regex.Pattern

fun String.replaceBlank(): String{
    val pattern = Pattern.compile("\\s*|\t|\r|\n")
    val matcher = pattern.matcher(this)
    return matcher.replaceAll("")
}

fun String.toBcd(): ByteArray {
    var asc = this
    var len = length
    val mod = len % 2
    if (mod != 0) {
        asc = "0$asc"
        len = asc.length
    }
    var abt: ByteArray
    if (len >= 2) {
        len /= 2
    }
    val bbt = ByteArray(len)
    abt = asc.toByteArray()
    var j: Int
    var k: Int
    for (p in 0 until asc.length / 2) {
        j = if (abt[2 * p] >= '0'.toByte() && abt[2 * p] <= '9'.toByte()) {
            abt[2 * p] - '0'.toByte()
        } else if (abt[2 * p] >= 'a'.toByte() && abt[2 * p] <= 'z'.toByte()) {
            abt[2 * p] - 'a'.toByte() + 0x0a
        } else {
            abt[2 * p] - 'A'.toByte() + 0x0a
        }
        k = if (abt[2 * p + 1] >= '0'.toByte() && abt[2 * p + 1] <= '9'.toByte()) {
            abt[2 * p + 1] - '0'.toByte()
        } else if (abt[2 * p + 1] >= 'a'.toByte() && abt[2 * p + 1] <= 'z'.toByte()) {
            abt[2 * p + 1] - 'a'.toByte() + 0x0a
        } else {
            abt[2 * p + 1] - 'A'.toByte() + 0x0a
        }
        val a = (j shl 4) + k
        val b = a.toByte()
        bbt[p] = b
    }
    return bbt
}