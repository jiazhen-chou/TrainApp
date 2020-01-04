package com.daohang.trainapp.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.getSystemService
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.daohang.trainapp.BuildConfig
import com.daohang.trainapp.MyApplication
import com.daohang.trainapp.R
import com.daohang.trainapp.constants.LOGOUT
import com.daohang.trainapp.constants.ProtocolSend
import com.daohang.trainapp.db.DaoHelper
import com.daohang.trainapp.db.models.*
import com.daohang.trainapp.services.*
import com.daohang.trainapp.ui.BaseActivity
import com.daohang.trainapp.ui.insertPassword.FLAG
import com.daohang.trainapp.ui.insertPassword.InsertPasswordActivity
import com.daohang.trainapp.ui.train.TrainActivity
import com.daohang.trainapp.ui.wifiSelect.WifiSelectActivity
import com.daohang.trainapp.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.component_header.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.io.File

class MainActivity : BaseActivity() {

    private var canConnectSocket = false
    private lateinit var viewModel: MainViewModel
    private var trainPreferenceEnable = false
    private lateinit var progressBar: ProgressDialog
    private var filePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        setLayoutId(R.layout.activity_main)
        setBackgroundResource(R.mipmap.lines_bg)

        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this)[MainViewModel::class.java]

        progressBar = ProgressDialog(this)
        progressBar.max = 100
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressBar.setMessage("正在下载更新")
        progressBar.setCancelable(false)

        initData()
    }

    fun onClick(view: View?) {
        view?.let {
            when (it) {
                btnPartTwo -> {
                    if (!isOnline.value!!)
                        toast("设备未上线")
                    else if (!trainPreferenceEnable)
                        toast("正在初始化项目信息...")
                    else
                        startActivity<TrainActivity>("className" to "第二部分")
                }
                btnPartThree -> {
                    if (!isOnline.value!!)
                        toast("设备未上线")
                    else if (!trainPreferenceEnable)
                        toast("正在初始化项目信息...")
                    else
                        startActivity<TrainActivity>("className" to "第三部分")
                }
                btnParameterSetting -> {
                    startActivity<InsertPasswordActivity>(FLAG to "setting")
                }
                btnWifiSetting -> {
                    startActivity<WifiSelectActivity>()
                }
                btnDesktop -> {
                    startActivity<InsertPasswordActivity>(FLAG to "desktop")
                }
                else -> Unit
            }
        }
    }

    override fun onBackPressed() {

    }

    @SuppressLint("MissingPermission", "HardwareIds", "SetTextI18n")
    private fun initData() {

        if (hasPermission(Manifest.permission.READ_PHONE_STATE)) {
            MyApplication.IMEI = getSystemService<TelephonyManager>()?.deviceId!!
            tvImei.text = "IMEI: ${MyApplication.IMEI}"
        } else {
            requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE), 1)
        }

        tvVersion.text = "VERSION: ${versionName()}"

        viewModel.newVersionInfo.observe(this, Observer {
            it?.run {
                if (version > BuildConfig.VERSION_CODE)
                    showNewVersionDialog(this)
            }
        })

        //获取项目信息，未获取到的情况下不会连接socket
        viewModel.getPreferenceModel().observe(this, Observer {
            if (it.isNotEmpty()) {
                it[0].run {
                    globalPreferenceModel = this

                    PASSWORD = passwordA
                    tvTitle.text = projectName
                    tvClientId.text = "SIM卡: ${vehiclePreference.clientId}"

//                    viewModel.initProjectApi(apiDomain, apiPort)
                    viewModel.initProjectApi("192.168.2.41", 8080)

                    viewModel.code = code
                    if (!viewModel.isDownloading)
                        viewModel.fetchNewVersionCode()

                    canConnectSocket = true

                    viewModel.getCertification()
                        .observe(this@MainActivity, Observer { registerModel ->
                            SocketClientService(
                                webSocketDomain,
                                webSocketPort,
                                registerModel
                            ).start()
                        })
                }
            }
        })

        viewModel.progress.observe(this, Observer {
            if (it == 100 && progressBar.isShowing) {
                progressBar.dismiss()
                toast("下载成功，正在安装，请稍后...")
                installApk(filePath)
            }

            if (it >= 0 && viewModel.isDownloading) {
                if (!progressBar.isShowing)
                    progressBar.show()

                progressBar.progress = it
            }
        })

        viewModel.trainPreferenceEnable.observe(this, Observer {
            trainPreferenceEnable = it
        })

        viewModel.getUnCompleteClass().observe(this, Observer {
            if (it.size == 2) {
//                showContinueDialog(it[0].className, it)
                startActivity<TrainActivity>("className" to it[0].className)
            }
        })

        viewModel.connectSocket()

        viewModel.getTrainParameter()
    }

    override fun onResume() {
        super.onResume()
        if (!viewModel.isDownloading)
            viewModel.fetchNewVersionCode()
    }

    @SuppressLint("MissingPermission", "HardwareIds", "SetTextI18n")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            MyApplication.IMEI = getSystemService<TelephonyManager>()?.deviceId!!
            tvImei.text = "IMEI: ${MyApplication.IMEI}"
        }
    }

    /**
     * 继续培训弹窗
     */
    private fun showContinueDialog(className: String, infos: List<SavedCardInfo>) {
        AlertDialog.Builder(this)
            .setMessage("您有未完成的培训，是否继续学习？")
            .setNegativeButton("否") { _, _ ->
                DaoHelper.History.delete()
                var student = infos.filter { it.type == 0 }[0]
                var coach = infos.filter { it.type == 1 }[0]

                logoutStudent(student, coachNumber = coach.studentNumber)
                logoutCoach(coach)
            }
            .setPositiveButton("是") { _, _ ->
                startActivity<TrainActivity>("className" to className, "continue" to true)
            }
            .setCancelable(false)
            .setOnCancelListener { DaoHelper.History.delete() }
            .show()
    }

    /**
     * 更新弹窗
     */
    private fun showNewVersionDialog(versionInfo: VersionInfo) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_new_version, null, false)
        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        view.findViewById<Button>(R.id.btnCancel).onClick { dialog.cancel() }
        view.findViewById<Button>(R.id.btnSubmit).onClick {
            filePath = "${updatePath()}/${versionInfo.version}.apk"
            viewModel.downloadNewApk(
                "http://${viewModel.domain}:${viewModel.port}/apk/${versionInfo.version}/${viewModel.code}/2", updatePath(), filePath)
            dialog.cancel()
            toast("开始下载新版本")
        }
        view.findViewById<TextView>(R.id.updateInfo).text = versionInfo.content
        dialog.show()
    }

    /**
     *  断电后不继续学习，进行学员和教练的签退
     */

    /**
     * 签退学员（不继续培训）
     */
    private fun logoutStudent(info: SavedCardInfo, coachNumber: String) {
        StudentStateModel(
            info.id - 1,
            LOGOUT,
            info.studentNumber,
            coachNumber,
            if (info.className == "第二部分") "1212130000" else "1213360000",
            (info.currentTrainTime) / 60,
            0,
            byteArrayOf()
        ).let {
            insertMessage(
                MessageModel(
                    sequenceNumber,
                    ProtocolSend.SEND_PENETRATE_MESSAGE,
                    studentLogout(it)
                )
            )
        }
    }

    /**
     * 签退教练（不继续培训）
     */
    private fun logoutCoach(info: SavedCardInfo) {
        CoachStateModel(
            info.studentNumber,
            info.identification,
            enumCarType(info.carType).name,
            LOGOUT,
            byteArrayOf()
        ).let {
            insertMessage(
                MessageModel(
                    sequenceNumber,
                    ProtocolSend.SEND_PENETRATE_MESSAGE,
                    coachLogout(it.coach_number)
                )
            )
        }
    }

    /**
     * 安装更新包
     */
    private fun installApk(filePath: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        val file = File(filePath)
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        startActivity(intent)
//        val commandResult: ShellUtils.CommandResult = ShellUtils.execCommand(
//            listOf(
//                "pm install -r $filePath",
//                "am start -n com.daohang.trainapp/com.daohang.trainapp.ui.selfcheck.SelfCheckActivity"
//            ), false
//        )
//
//        Log.e("MainActivity",commandResult.toString())
    }
}
