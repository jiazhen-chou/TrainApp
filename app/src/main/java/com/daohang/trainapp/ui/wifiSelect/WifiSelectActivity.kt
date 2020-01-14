package com.daohang.trainapp.ui.wifiSelect

import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import androidx.core.content.getSystemService
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.daohang.trainapp.R
import com.daohang.trainapp.adapters.WifiSelectRecyclerAdapter
import com.daohang.trainapp.db.models.WifiScanResult
import com.daohang.trainapp.receiver.WifiScanReceiver
import com.daohang.trainapp.ui.BaseActivity
import com.daohang.trainapp.constants.WIFI_SCAN_RESULT
import com.daohang.trainapp.utils.hasPermission
import com.jeremyliao.liveeventbus.LiveEventBus
import kotlinx.android.synthetic.main.activity_wifi_select.*
import kotlinx.android.synthetic.main.component_header.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.wifiManager

class WifiSelectActivity : BaseActivity() {

    lateinit var adapter: WifiSelectRecyclerAdapter
    private var mWifiManager: WifiManager? = null
    lateinit var scanReceiver: WifiScanReceiver
    private var currentConnectedSSID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        setLayoutId(R.layout.activity_wifi_select)

        super.onCreate(savedInstanceState)

        tvTitle.text = getString(R.string.title_wifi_setting)

        mWifiManager = getSystemService()

        initView()

        scanReceiver = WifiScanReceiver(wifiManager)

        LiveEventBus.get(WIFI_SCAN_RESULT, Array<WifiScanResult>::class.java)
            .observe(this, Observer {
                adapter.setDataList(currentConnectedSSID, it.toList())
            })
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(scanReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))

        if (hasPermission(Manifest.permission.CHANGE_WIFI_STATE)) {
            startScan()
        } else {
            requestPermissions(arrayOf(Manifest.permission.CHANGE_WIFI_STATE), 1)
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(scanReceiver)
    }

    private fun initView() {

        lvWifiList.layoutManager = LinearLayoutManager(this)
        adapter = WifiSelectRecyclerAdapter(this, mutableListOf())
        lvWifiList.adapter = adapter

        btnRefresh.onClick {
            adapter.setDataList(currentConnectedSSID, listOf())
            startScan()
        }

        btnBack.onClick {
            finish()
        }
    }

    private fun startScan() {
        getSystemService<ConnectivityManager>()?.let {
            if (!it.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting)
                mWifiManager?.isWifiEnabled = true
            mWifiManager?.startScan()
            val ssid = mWifiManager?.connectionInfo?.ssid
            ssid?.run {
                currentConnectedSSID = ssid.substring(1 until length - 1)
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startScan()
        } else {
            requestPermissions(arrayOf(Manifest.permission.CHANGE_WIFI_STATE), 1)
        }
    }
}