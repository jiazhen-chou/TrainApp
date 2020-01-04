package com.daohang.trainapp.enums

enum class LoginState(val str: String) {

    NOSTATE("无状态"),
    COACH_LOGIN("教练签到"),
    STUDENT_LOGIN("学员签到"),
    STUDENT_LOGOUT("学员签退"),
    COACH_LOGOUT("教练签退"),
    TRAINING("培训中"),
    STOPPED("培训结束")

}