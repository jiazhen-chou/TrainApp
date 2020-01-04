package com.daohang.trainapp.db.models

data class GeoFenceModel(
    //围栏串
    val polygon: String,
    val regionId: Int,
    //培训科目
    val subject: Int
)

data class Point(val x: Int, val y: Int)