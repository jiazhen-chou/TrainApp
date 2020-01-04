package com.daohang.trainapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.daohang.trainapp.MyApplication
import com.daohang.trainapp.R
import com.daohang.trainapp.constants.*
import com.daohang.trainapp.db.DaoHelper
import com.daohang.trainapp.db.models.RegisterModel
import com.daohang.trainapp.livebus.LocationStates
import com.daohang.trainapp.livebus.NetworkStates
import com.daohang.trainapp.livebus.TimeStates
import com.daohang.trainapp.livebus.TtsMessage
import com.daohang.trainapp.services.isOnline
import com.daohang.trainapp.utils.*
import com.jeremyliao.liveeventbus.LiveEventBus
import kotlinx.android.synthetic.main.component_header.*
import org.jetbrains.anko.*

var registerInfo: RegisterModel? = null

abstract class BaseActivity : AppCompatActivity() {

    private var layoutId: Int = 0
    private var titleVisible = View.VISIBLE
    private var background1: Int = R.mipmap.all_bg
    private var backgroundNormal: Int = R.mipmap.bg
    private var backGroundVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaoHelper.Register.getRegisterInfo().observe(this, Observer {
            registerInfo = it
        })

        frameLayout {
            backgroundColor = R.color.bg_black
            verticalLayout {
                backgroundResource = if (backGroundVisible) background1 else backgroundNormal
                include<View>(R.layout.component_header)

                verticalLayout {
                    include<View>(layoutId)
                }.lparams(width = matchParent, height = matchParent) {
                    topMargin = dip(13)
                }
            }
        }

        init()

        lnTitle.visibility = titleVisible

        checkPermission()
    }


    override fun onResume() {
        super.onResume()

        observeLocation()
        observeNetwork()
        observeTime()
        observeTtsMessage()

        isOnline.observe(this, Observer {
            println(if (it) "在线" else "离线")
        })
    }

    override fun onPause() {
        super.onPause()
        isOnline.removeObservers(this)
    }

    private fun init() {
        changeStateImage(if (MyApplication.networkEnabled) R.mipmap.net_icon else R.mipmap.disable_net_icon)
        changeTime(MyApplication.time)
        changeGpsStateImage(if (MyApplication.locationEnabled) R.mipmap.location_icon else R.mipmap.disable_location_icon)
    }

    /**
     * 语音播报
     */
    private fun observeTtsMessage() {
        LiveEventBus.get(SEND_TTS_MESSAGE, TtsMessage::class.java)
            .observe(this, Observer {
                SoundManager.startPlay(it.msg)
            })
    }

    /**
     * 监控网络状态
     */
    private fun observeNetwork() {
        LiveEventBus.get(NETWORK_STATE_CHANGE, NetworkStates::class.java)
            .observe(this, Observer {

                val netResId =
                    if (it.networkEnabled) R.mipmap.net_icon else R.mipmap.disable_net_icon
                changeStateImage(netResId)
            })
    }

    /**
     * 监控时间变化
     */
    private fun observeTime() {
        LiveEventBus.get(TIME_STATE_CHANGE, TimeStates::class.java)
            .observe(this, Observer {
                val time = it.time
                changeTime(time)
            })
    }

    /**
     * 监控gps卫星信号
     */
    private fun observeLocation() {
        LiveEventBus.get(GPS_STATE_CHANGE, LocationStates::class.java)
            .observe(this, Observer {
                val locationResId =
                    if (it.canLocate) R.mipmap.location_icon else R.mipmap.disable_location_icon
                changeGpsStateImage(locationResId)
            })
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_LOCATION
            )
        } else if (!MyApplication.locationStarted) {
            startLocation(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!MyApplication.locationStarted)
                        startLocation(this)
                } else {
                    toast("定位服务未授权，无法使用")
                }
            }
        }
    }

    fun setBackgroundVisible(visible: Boolean) {
        backGroundVisible = visible
    }

    fun setLayoutId(id: Int) {
        layoutId = id
    }

    fun setTitleVisible(visible: Int) {
        titleVisible = visible
    }

    fun setBackgroundResource(resId: Int) {
        background1 = resId
    }

    private fun changeTime(time: String) {
        tvTime.text = time
    }

    private fun changeGpsStateImage(locationResId: Int) {
        ivLocationState.imageResource = locationResId
    }

    private fun changeStateImage(netResId: Int) {
        ivNetworkState.imageResource = netResId
    }
}