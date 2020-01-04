package com.daohang.trainapp.db.models

data class FaceSearchResponseModel(
    //驾校编号
    val inscode: String,
    //驾校名称
    val insName: String,
    //2--培训部分二，3--培训部分三
    val subject: Byte,
    //1--学员，2--教练
    val type: Byte,
    //用户姓名
    val userName: String,
    //培训部分标准学时
    val minSubHour: Int,
    //培训部分标准里程
    val minSubMil: Int,
    //当日已培训学时
    val daySum: Int,
    //学员/教练编号
    val usercode: String,
    //身份证号
    val idcard: String,
    //车型
    val trainType: String
) {
    override fun toString(): String {
        return "驾校编号：$inscode, " +
                "驾校名称：$insName, " +
                "培训部分：$subject, " +
                "类型：$type, " +
                "用户姓名：$userName, " +
                "标准学时：$minSubHour, " +
                "标准里程: $minSubMil, " +
                "当日学时：$daySum, " +
                "编号：$usercode, " +
                "身份证号；$idcard, " +
                "车型：$trainType"
    }
}