package com.daohang.trainapp.services

import android.util.Log
import com.daohang.trainapp.constants.ProtocolSend
import com.daohang.trainapp.db.DaoHelper
import com.daohang.trainapp.db.models.MessageModel
import com.daohang.trainapp.db.models.RegisterModel
import com.daohang.trainapp.services.ServerMessages.handleMessageReceived
import com.daohang.trainapp.utils.toByteArray2
import com.daohang.trainapp.utils.toHexString
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import moe.codeest.rxsocketclient.RxSocketClient
import moe.codeest.rxsocketclient.SocketClient
import moe.codeest.rxsocketclient.SocketSubscriber
import moe.codeest.rxsocketclient.meta.SocketConfig
import moe.codeest.rxsocketclient.meta.SocketOption
import moe.codeest.rxsocketclient.meta.ThreadStrategy
import java.util.*

var socketClient: SocketClient? = null

class SocketClientService(val ip: String, val port: Int, val model: RegisterModel?) : Thread() {

    val TAG = "SocketClientService"

    override fun run() {
        connectToSocket()
    }

    private fun connectToSocket() {
        println("开始连接socket")

        val builder = SocketConfig.Builder()
            .setIp(ip)
            .setPort(port)
            .setThreadStrategy(ThreadStrategy.ASYNC)
            .setTimeout(30 * 1000)
            .build()

        val option = SocketOption.Builder()
            .setHeartBeat(sendClientMessage(ProtocolSend.SEND_HEARTBEAT, byteArrayOf()), 10 * 1000)
            .build()

        socketClient = RxSocketClient.create(builder).option(option)

        socketClient?.run {
            connect()
                .observeOn(Schedulers.newThread())
                .subscribe(object : SocketSubscriber() {
                    override fun onConnected() {
                        Log.d(TAG, "已连接")

                        if (model != null) {
                            Log.d(TAG, "证书已存在")
                            sendProtocolData(validate(model))
                        } else {
                            Log.d(TAG, "证书不存在")
                            sendProtocolData(register())
                        }

                        if (!sendTimerStarted) {
                            sendTimerStarted = true
                            startLocationTimer()
                            startSendTimer()
                        }

                        canSendNext = true
                    }

                    override fun onDisconnected() {
                        Log.d(TAG, "已断开连接")
                        isOnline.postValue(false)
                        sleep(2000)
                        connectToSocket()
                    }

                    override fun onResponse(data: ByteArray) {
                        Log.d(TAG, "接收数据长度：${decode(data).size}")
                        Log.d(TAG, "接收数据：${decode(data).toHexString()}")
                        handleMessageReceived(data)
                    }
                }, Consumer {
                    Log.e("SocketClientService", it.toString())
                })
        }
    }

    /**
     * 定时插入位置信息
     */
    fun startLocationTimer() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                insertMessage(MessageModel(sequenceNumber, ProtocolSend.SEND_LOCATION, locate()))
            }
        }, 0, 10 * 1000)
    }

    /**
     * 循环发送消息
     */
    fun startSendTimer() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (canSendNext && !isInsertingPicture) {
                    DaoHelper.Message.getLatestMessage()?.run {
                        Log.d(TAG, "发送消息序列号：${sequenceNum}")
                        println("发送消息Id：0x${messageId.toByteArray2().toHexString()}")

                        if (isOnline.value!!)
                            sendProtocolData(messageContent)
                        canSendNext = false
                        currentModelId = id

                        if (messageId == ProtocolSend.SEND_HEARTBEAT)
                            canSendNext = true
                    }
                }
            }
        }, 0, 200)
    }
}

fun sendProtocolData(bytes: ByteArray) {
    Log.d("SendData", "发送消息：${bytes.toHexString()}")
    socketClient?.sendData(bytes)
}