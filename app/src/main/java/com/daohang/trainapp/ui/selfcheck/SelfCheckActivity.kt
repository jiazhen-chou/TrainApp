package com.daohang.trainapp.ui.selfcheck

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.daohang.trainapp.R
import com.daohang.trainapp.constants.*
import com.daohang.trainapp.ui.BaseActivity
import com.daohang.trainapp.ui.WelcomeActivity
import com.daohang.trainapp.ui.projectSelect.ProjectSelectActivity
import kotlinx.android.synthetic.main.activity_selfcheck.*
import kotlinx.android.synthetic.main.component_header.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class SelfCheckActivity : BaseActivity() {
    private val viewModel by lazy { ViewModelProviders.of(this)[SelfCheckViewModel::class.java] }
    private val errorList = mutableListOf<String>()
    private var hasBeenInitialized: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {

        //一定要在super.onCreate()之前调用，否则空指针
        setLayoutId(R.layout.activity_selfcheck)

        super.onCreate(savedInstanceState)

        tvTitle.text = getString(R.string.title_selfcheck)

        printPixel()

        btnReCheck.onClick { check() }

        viewModel.preferenceCount.observe(this, Observer {
            hasBeenInitialized = it > 0
            if (hasBeenInitialized)
                viewModel.getTrainParameter()
            check()
        })

        viewModel.checkResult.observe(this, Observer {
            when (it.first) {
                RFID_CHECK_NUM -> {
                    tvRfidState.text = if (it.second) "正常" else "异常"
                    tvRfidState.textColorResource =
                        if (it.second) R.color.white else R.color.error_red
                    loader_rfid.visibility = View.INVISIBLE
                    if (!it.second)
                        errorList.add(tvRfid.text.toString())
                }
                GPS_CHECK_NUM -> {
                    tvGpsState.text = if (it.second) "正常" else "异常"
                    tvGpsState.textColorResource =
                        if (it.second) R.color.white else R.color.error_red
                    loader_gps.visibility = View.INVISIBLE
                    if (!it.second)
                        errorList.add(tvGps.text.toString())
                }
                CAMERA_CHECK_NUM -> {
                    tvCameraState.text = if (it.second) "正常" else "异常"
                    tvCameraState.textColorResource =
                        if (it.second) R.color.white else R.color.error_red
                    loader_camera.visibility = View.INVISIBLE
                    if (!it.second)
                        errorList.add(tvCamera.text.toString())
                }
                OBD_CHECK_NUM -> {
                    tvObdState.text = if (it.second) "正常" else "异常"
                    tvObdState.textColorResource =
                        if (it.second) R.color.white else R.color.error_red
                    loader_obd.visibility = View.INVISIBLE
                    if (!it.second)
                        errorList.add(tvObd.text.toString())
                }
                NETWORK_CHECK_NUM -> {
                    tvNetworkState.text = if (it.second) "正常" else "异常"
                    tvNetworkState.textColorResource =
                        if (it.second) R.color.white else R.color.error_red
                    loader_network.visibility = View.INVISIBLE
                    if (!it.second)
                        errorList.add(tvNetwork.text.toString())
                }
                COMPLETE_CHECK -> {
                    if (errorList.isNotEmpty()){
                        lnErrorMessage.visibility = View.VISIBLE
                        val result = "注：" + errorList.reduce{ s1, s2 -> "$s1、$s2"} + "异常，请联系陕西导航售后"
                        tvUnNormal.text = result
                    }else{
                        lnErrorMessage.visibility = View.INVISIBLE
                        goNext()
                    }
                    errorList.clear()
                }
            }
        })
    }

    private fun goNext(){
        if (hasBeenInitialized)
            startActivity<WelcomeActivity>()
        else
            startActivity<ProjectSelectActivity>()
        finish()
    }

    private fun printPixel(){
        (getSystemService(Context.WINDOW_SERVICE) as WindowManager).let {
            val metrics = DisplayMetrics()
            it.defaultDisplay.getMetrics(metrics)
            println("width: ${metrics.widthPixels}, height: ${metrics.heightPixels}")
        }
    }


    private fun check(){
        lnErrorMessage.visibility = View.INVISIBLE

        loader_rfid.visibility = View.VISIBLE
        loader_gps.visibility = View.VISIBLE
        loader_camera.visibility = View.VISIBLE
        loader_obd.visibility = View.VISIBLE
        loader_network.visibility = View.VISIBLE

        tvRfidState.text = ""
        tvGpsState.text = ""
        tvCameraState.text = ""
        tvObdState.text = ""
        tvNetworkState.text = ""

        viewModel.checkState()
    }
}