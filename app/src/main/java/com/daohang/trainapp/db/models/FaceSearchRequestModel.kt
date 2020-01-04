package com.daohang.trainapp.db.models

/**
 * 人脸识别请求数据
 */
data class FaceSearchRequestModel(
    //照片数据（base64）
    val image: String,
    //驾校编号
    val inscode: String,
    //1--学员，2--教练
    val type: Byte,
    //2--培训部分二，3--培训部分三
    val subject: Byte,
    //学员/教练编号
    val num: String
)