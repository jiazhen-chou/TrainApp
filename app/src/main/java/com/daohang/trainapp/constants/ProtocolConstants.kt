package com.daohang.trainapp.constants

object ProtocolSend {
    //客户端通用应答
    const val SEND_CLIENT_COMMON = 0x0001
    //客户端心跳
    const val SEND_HEARTBEAT = 0x0002
    //终端注册
    const val SEND_REGISTER = 0x0100
    //终端注销
    const val SEND_LOGOUT = 0x0003
    //终端鉴权
    const val SEND_VALIDATION = 0x0102
    //位置信息
    const val SEND_LOCATION = 0x0200
    //教练员登录
    const val SEND_COACH_LOGIN = 0x0101
    //教练员登出
    const val SEND_COACH_LOGOUT = 0X0102
    //学员登录
    const val SEND_STU_LOGIN = 0x0201
    //学员登出
    const val SEND_STU_LOGOUT = 0x0202
    //上报学时记录
    const val SEND_MINUTE_RECORD = 0x0203
    //查询终端参数应答
    const val SEND_QUERY_PARAMETER = 0x0104
    //上行透传协议
    const val SEND_PENETRATE_MESSAGE = 0X0900
    //照片上传初始化
    const val SEND_PICTURE_INIT = 0x0305
    //上传照片数据包
    const val SEND_PICTURE = 0x0306
}

object ProtocolReceive{
    //下行透传协议
    const val RECEIVE_PENETRATE_MESSAGE = 8900
    //服务端通用应答
    const val RECEIVE_SERVER_COMMON = 8001
    //终端注册应答
    const val RECEIVE_REGISTER = 8100
    //设置终端参数
    const val RECEIVE_SET_PARAMETER = 8103
    //查询终端参数
    const val RECEIVE_QUERY_PARAMETER = 8104
    //查询指定终端参数
    const val RECEIVE_QUERY_CERTAIN_PARAMETER = 8106
    //终端控制
    const val RECEIVE_TERMINAL_CONTROL = 8105
    //位置信息查询
    const val RECEIVE_QUERY_LOCATION = 8201


    //驾培扩展协议
    //学员登录应答
    const val RECEIVE_EXTEND_STUDENT_LOGIN = 0x8201
    //教练登录应答
    const val RECEIVE_EXTEND_COACH_LOGIN = 0x8101
}