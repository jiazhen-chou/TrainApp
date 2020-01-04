package com.daohang.trainapp.db.models

import java.util.*

abstract class BaseModel{
    //记录产生时间
    var time: Long = Date().time
    //是否为盲区数据
    var isBlindArea: Boolean = false
    //是否已上传
    var hasUploaded: Boolean = false
}