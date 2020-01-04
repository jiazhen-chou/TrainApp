package com.daohang.trainapp.services

import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket

class NewSocketService : Thread(){

    lateinit var socket: Socket

    override fun run() {

        socket = IO.socket("http://192.168.2.88:7623")
        socket.on(Socket.EVENT_CONNECT) {
            println("socket连接： ${it[0]}")
        }

        socket.on(Socket.EVENT_CONNECT_ERROR){
            println("socket错误： ${it[0]}")
        }
    }
}