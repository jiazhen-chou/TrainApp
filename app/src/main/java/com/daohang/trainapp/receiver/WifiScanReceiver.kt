package com.daohang.trainapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import com.daohang.trainapp.db.models.WifiScanResult
import com.daohang.trainapp.constants.WIFI_SCAN_RESULT
import com.jeremyliao.liveeventbus.LiveEventBus

class WifiScanReceiver(val wifiManager: WifiManager) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            if (it.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION){
                val rssi = wifiManager.connectionInfo.rssi
                val resultList: MutableList<WifiScanResult> = mutableListOf()

                for (s in wifiManager.scanResults){
                    resultList.add(WifiScanResult(s.SSID, WifiManager.calculateSignalLevel(rssi, s.level)))
                }

                LiveEventBus.get(WIFI_SCAN_RESULT)
                    .post(resultList.toTypedArray())
            }
        }
    }

}