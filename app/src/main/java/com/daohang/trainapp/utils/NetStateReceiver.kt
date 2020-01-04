package com.daohang.trainapp.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

class NetStateReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val service = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCapabilities = service.getNetworkCapabilities(service.activeNetwork)
    }
}