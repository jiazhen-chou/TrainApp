package com.daohang.trainapp.services

import androidx.lifecycle.MutableLiveData
import com.daohang.trainapp.db.models.LocationModel

var sequenceNumber = 0
var extentedSequenceNumber = 0

//当前培训课程
var currentClassItem: Byte = 0
//是否正在培训
var isTraining = false
//最近一次定位结果
var currentLocation: LocationModel? = null

//标志位，发送消息后置为false，收到应答后置为true
//发送不需要应答的消息时忽略
var canSendNext = true
//当前处理的消息数据库id，用于更新消息状态
var currentModelId = 0
//消息发送定时任务是否已启动
var sendTimerStarted = false
//用户是否已登录
var isOnline = MutableLiveData<Boolean>(false)
//是否正在插入照片分包数据
var isInsertingPicture = false