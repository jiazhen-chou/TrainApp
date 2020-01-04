package com.daohang.trainapp.utils

import java.util.regex.Pattern

fun String.replaceBlank(): String{
    val pattern = Pattern.compile("\\s*|\t|\r|\n")
    val matcher = pattern.matcher(this)
    return matcher.replaceAll("")
}