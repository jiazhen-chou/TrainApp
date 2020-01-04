package com.daohang.trainapp.constants

//刷卡-教练员签到
const val COACH_CARD_LOGIN = "请教练员刷卡签到"
//刷卡-教练员签退
const val COACH_CARD_LOGOUT = "请教练员刷卡签退"
//刷卡-教练员签到成功
const val COACH_CARD_LOGIN_SUCCESS = "教练员刷卡成功，请学员刷卡签到"
//卡+人脸-教练员人脸识别签到
const val COACH_BOTH_FACE = "教练员刷卡成功，请正对摄像头开始人脸识别"

//人脸-教练员
const val COACH_FACE = "请教练员正对摄像头开始人脸识别"
//人脸-学员签到
const val STUDENT_FACE = "请学员正对摄像头开始人脸识别"

//刷卡-学员签到
const val STUDENT_CARD_LOGIN = "请学员刷卡签到"
//刷卡-学员签退
const val STUDENT_CARD_LOGOUT = "请学员刷卡签退"
//卡+人脸-学员人脸识别签到
const val STUDENT_BOTH_FACE = "学员刷卡成功，请正对摄像头开始人脸识别"


//教练员签到成功
const val COACH_LOGIN_SUCCESS = "教练员签到成功"
//签到成功，开始培训
const val LOGIN_SUCCESS = "学员签到成功，开始培训"
//签到失败
const val LOGIN_FAIL = "签到失败"
//学员签退成功
const val STUDENT_LOGOUT_SUCCESS = "学员签退成功"
//教练员签退成功，结束培训
const val COACH_LOGOUT_SUCCESS = "教练员签退成功，培训结束"
//签退失败
const val LOGOUT_FAIL = "签退失败"

//刷卡失败
const val CARD_FAIL = "刷卡失败"

const val FACE_FAIL = "人脸识别失败"

//签退时教练不是本人
const val WRONG_COACH = "不是教练本人，签退失败"
//签退时学员不是本人
const val WRONG_STUDENT = "不是学员本人，签退失败"

//不是教练卡
const val NOT_COACH = "不是教练卡"
//不是学员卡
const val NOT_STUDENT = "不是学员卡"
//教练准教车型不符
const val COACH_CARTYPE_ERROR = "教练准教车型不符"

const val WRITTING_CARD = "正在写卡，请稍候"

const val WRITTING_CARD_FAIL = "写卡失败，请重试"

const val WRITTING_CARD_SUCCESS = "写卡成功，请正对摄像头开始人脸识别"

const val OVER_MAX_TIME_TODAY = "您当日培训时长已满，是否继续培训？"