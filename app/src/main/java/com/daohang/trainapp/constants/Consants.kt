package com.daohang.trainapp.constants

//百度人脸识别grant_type
const val BAIDU_GRANT_TYPE = "client_credentials"
//百度人脸识别api key
const val BAIDU_AK = "FGsnda5kZfxMTusWf8QSiN55"
//百度人脸识别secret key
const val BAIDU_SK = "E1eRHEG2HMDpoSueEN8DqAwROjnU024r"

const val NETWORK_STATE_CHANGE = "networkStateChange"

const val TIME_STATE_CHANGE = "timeChange"

const val GPS_STATE_CHANGE = "gpsStateChange"

const val SEND_TTS_MESSAGE = "ttsMessage"

const val WIFI_SCAN_RESULT = "wifiScanResult"

const val PERMISSIONS_REQUEST_LOCATION = 1

const val DEVICE_1700 = 1700

const val DEVICE_1800 = 1800

var GPS_ENABLED = false

const val RFID_CHECK_NUM = 1
const val GPS_CHECK_NUM = 2
const val CAMERA_CHECK_NUM = 3
const val OBD_CHECK_NUM = 4
const val NETWORK_CHECK_NUM = 5
const val COMPLETE_CHECK = -1

const val LOGIN = 0
const val LOGOUT = 1

//const val CAR_TYPE = arrayOf("A1","A2","A3","B1","B2","B3","C1","C2","C3","C4")

//sharedpreference保存验证方式
const val SP_VALIDATION = "sp_validation"
//验证方式
const val VALIDATION_TYPE = "validation_type"
//人脸识别
const val VALIDATE_FACE = 0
//刷卡识别
const val VALIDATE_CARD = 1
//人脸+刷卡
const val VALIDATE_BOTH = 2

//存储学时记录编号
const val SP_RECORD_ID = "sp_record_id"
//学时记录编号
const val RECORD_ID = "record_id"
//保存日期
const val RECORD_TIME = "record_time"

//单包照片数据长度
const val PHOTO_PACKAGE_SIZE = 600

//定时拍照
const val EVENT_TYPE_ON_TIME: Byte = 5
//学员登录拍照
const val EVENT_TYPE_STUDENT_LOGIN: Byte = 17
//学员登出拍照
const val EVENT_TYPE_STUDENT_LOGOUT: Byte = 18
//培训过程中拍照
const val EVENT_TYPE_PROGRESSING: Byte = 19
//教练登录拍照
const val EVENT_TYPE_COACH_LOGIN: Byte = 20
//教练登出拍照
const val EVENT_TYPE_COACH_LOGOUT: Byte = 21

//第二部分
const val PART_TWO : Byte = 2
//第三部分
const val PART_THREE : Byte = 3