package com.daohang.trainapp.ui.train

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.camera.core.ImageCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.daohang.trainapp.R
import com.daohang.trainapp.components.CameraPreview
import com.daohang.trainapp.constants.*
import com.daohang.trainapp.db.DaoHelper
import com.daohang.trainapp.db.models.*
import com.daohang.trainapp.enums.LoginState
import com.daohang.trainapp.interfaces.OnMyClickListener
import com.daohang.trainapp.services.*
import com.daohang.trainapp.ui.BaseActivity
import com.daohang.trainapp.ui.registerInfo
import com.daohang.trainapp.utils.*
import com.yz.lz.modulapi.CardInfo
import com.yz.lz.modulapi.CardType
import kotlinx.android.synthetic.main.activity_train.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

private const val REQUEST_CODE_PERMISSIONS = 10

private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

class TrainActivity : BaseActivity() {
    private val TAG = "TrainActivity"

    private val viewModel by lazy {
        ViewModelProviders.of(this)[TrainViewModel::class.java]
    }

    //认证方式
    private var validationType: Int = globalPreferenceModel.authType
    //培训开始时的里程
    private var startDistance: Double = 0.0
    //已培训里程（单位米）
    private var distance: Double = 0.0
    //分钟里程
    private var minuteDistance: Double = 0.0
    //一分钟内最大速度
    private var minuteMaxSpeed = 0.0

    private lateinit var imageCapture: ImageCapture

    //培训课程名(第二部分，第三部分)
//    private var className = ""
    //组id，用于人脸识别
    private var groupId = ""
    //拍照间隔
    private var photoInterval = 15 * 60
    //车牌号
    private var carNum = ""
    //车型列表
    private var carMap = HashMap<String, String>()
    //当前设备准教车型
    private var carType = ""
    //已培训学时（分钟）
    private var trainedTime = 0
    //培训开始时的已培训时长(分钟)
    private var startTime = 0
    //当天还剩余培训时间
    private var timeLeftToday = -1
    //当天已培训时长
    private var todayPraticeTime = 0
    //一天最长培训时长
    private var maxTimePerDay = 0

    private var mCamera: Camera? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        setLayoutId(R.layout.activity_train)
        setTitleVisible(View.INVISIBLE)
        setBackgroundVisible(false)

        super.onCreate(savedInstanceState)

        viewModel.setState(LoginState.NOSTATE)


        // 获取未完成课程
        viewModel.getUnCompleteClass().observeOnce(this, Observer {
            var studentModel: SavedCardInfo? = null
            var coachModel: SavedCardInfo? = null

            if (it.size == 2) {
                it.forEach { info ->
                    //学员卡
                    if (info.type == 0) {
                        studentModel = info
                        viewModel.classId = info.id
                        viewModel.studentModel = CardInfo(info)
                        itemStudent.setName(info.name)
                            .setId(info.identification)
                            .setLoginStatus(true)
                    }
                    //教练卡
                    else {
                        coachModel = info
                        viewModel.coachModel = CardInfo(info)
                        itemCoach.setName(info.name)
                            .setId(info.identification)
                            .setLoginStatus(true)
                    }
                }
                showContinueDialog(studentModel!!)
            }
        })

        //获取电子围栏
        viewModel.fetchGeoFence()

        if (allPermissionGranted()) {
            surfaceView.post { startCamera() }
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        initView()
    }

    private fun initView() {
        viewModel.className = intent.getStringExtra("className")

        tvClassName.text = viewModel.className

        btnQuit.onClick {
            finish()
        }

        itemStudent.setOnClick(object : OnMyClickListener {
            override fun onClick() {
                if (itemCoach.status) {
                    if (itemStudent.status) {
                        viewModel.setState(LoginState.STUDENT_LOGOUT)
                    } else {
                        if (isOnline.value == false && validationType != VALIDATE_CARD)
                            toast("无法连接服务器")
                        else
                            viewModel.setState(LoginState.STUDENT_LOGIN)
                    }
                }
            }
        })

        itemCoach.setOnClick(object : OnMyClickListener {
            override fun onClick() {
                if (itemCoach.status && !itemStudent.status) {
                    viewModel.setState(LoginState.COACH_LOGOUT)
                } else if (itemCoach.status && itemStudent.status) {
                    toast("请先签退学员")
                } else if (!itemCoach.status) {
                    if (isOnline.value == false && validationType != VALIDATE_CARD)
                        toast("无法连接服务器")
                    else
                        viewModel.setState(LoginState.COACH_LOGIN)
                }
            }
        })

        //经度
        lastLongitude.observe(this, Observer {
            tvLongitude.text = String.format("%.6f", it)
        })

        //纬度
        lastLatitude.observe(this, Observer {
            tvLatitude.text = String.format("%.6f", it)
        })

        //
        currentDistance.observe(this, Observer {
            if (isTraining) {
                distance += it
                val trueDistance = distance / 1000
                tvTotalMiles.text =
                    "${if (trueDistance == 0.0) 0 else DecimalFormat("#.000").format(trueDistance)}公里"
            }
        })

        currentSpeed.observe(this, Observer {
            if (isTraining) {
                tvSpeed.text = "${if (it == 0.0) 0.0 else DecimalFormat("#.0").format(it)}"
                if (it > minuteMaxSpeed)
                    minuteMaxSpeed = it
            }
        })

        viewModel.countDown.observe(this, Observer {
            tvCountDown.text = it

            if (viewModel.count != 0 && viewModel.count % 10 == 0)
                viewModel.saveRecord(distance, trainedTime)

            if (maxTimePerDay - todayPraticeTime - viewModel.count / 60 <= 0) {
                sendTTSMsg(OVER_MAX_TIME_TODAY)
            }

            if (viewModel.count != 0 && viewModel.count % 60 == 0) {
                insertRecord()
                trainedTime += 1
                tvTotalTime.text = formatTime(trainedTime)
            }
            if (viewModel.count != 0 && photoInterval != 0 && viewModel.count % photoInterval == 0) {
                captureInProgress(EVENT_TYPE_ON_TIME, viewModel.coachModel, viewModel.studentModel)
            }
        })

        globalPreferenceModel.let {
            tvCarNum.text = it.vehiclePreference.vehicleNumber
            carNum = it.vehiclePreference.vehicleNumber
            carType = it.vehiclePreference.vehicleType
//            validationType = it.authType
            validationType = VALIDATE_BOTH
        }

        /**
         * 已培训学时和已培训里程
         * 两种获取方式
         * 1.登录时通过下发命令获取
         * 2.刷卡时从卡中读取
         */
        timeAndMilesModel.observe(this, Observer {
            tvTotalMiles.text = "${DecimalFormat("#.000").format(it.currentMiles.toFloat() / 10)}公里"
            distance = (it.currentMiles * 100).toDouble()
            startDistance = distance
            trainedTime = it.currentTime
            startTime = it.currentTime
            tvTotalTime.text = formatTime(it.currentTime)
        })

        viewModel.trainPreferenceLiveData.observe(this, Observer {
            photoInterval = it.picture_interval * 60
            carMap.clear()
            it.check_car_type.split(";").forEach { car ->
                val item = car.split(":")
                carMap[item[0]] = item[1]
            }
            maxTimePerDay = it.max_hour
        })

        viewModel.cardInfoLiveData.observe(this, Observer {
            when (viewModel.loginState.value) {
                LoginState.COACH_LOGIN -> {
                    when (it.cardType) {
                        CardType.CommonCoachCard,
                        CardType.PracticeCoachCard -> {
                            if (verifyCarType(it)) {
                                viewModel.coachModel = it
                                sendTTSMsg(COACH_CARD_LOGIN_SUCCESS)

                                if (validationType == VALIDATE_BOTH)
                                    loginByFace()
                                else
                                    setInfo()
                            } else {
                                sendTTSMsg(COACH_CARTYPE_ERROR)
                            }
                        }
                        else -> {
                            sendTTSMsg(NOT_COACH)
                        }
                    }
                }

                LoginState.COACH_LOGOUT -> {
                    when (it.cardType) {
                        CardType.CommonCoachCard,
                        CardType.PracticeCoachCard -> {
                            if (!verifySamePerson(viewModel.coachModel, it)) {
                                sendTTSMsg(WRONG_COACH)
                            } else {
                                if (validationType == VALIDATE_BOTH)
                                    loginByFace()
                                else {
                                    sendTTSMsg(COACH_LOGOUT_SUCCESS)
                                    setInfo()
                                }
                            }
                        }
                        else -> {
                            sendTTSMsg(NOT_COACH)
                        }
                    }
                }

                LoginState.STUDENT_LOGIN -> {
                    if (it.cardType == CardType.StudentCard) {
                        viewModel.studentModel = it

                        if (isOnline.value == false) {
                            when (viewModel.className) {
                                "第二部分" -> timeAndMilesModel.postValue(
                                    TimeAndMilesModel(
                                        it.subjectTwoTotalTime,
                                        it.subjectTwoLearnedTime,
                                        it.subjectTwoTotalMiles,
                                        it.subjectTwoLearnedMiles
                                    )
                                )
                                "第三部分" -> timeAndMilesModel.postValue(
                                    TimeAndMilesModel(
                                        it.subjectThreeTotalTime,
                                        it.subjectThreeLearnedTime,
                                        it.subjectThreeTotalMiles,
                                        it.subjectThreeLearnedMiles
                                    )
                                )
                                else -> Unit
                            }
                        }

                        //验证上次签到时间是否与当前时间为同一天
                        todayPraticeTime = if (isInSameDay(it.lastExitDate)) {
                            it.practiceTime
                        } else {
                            0
                        }
                        if (validationType == VALIDATE_BOTH)
                            loginByFace()
                        else
                            setInfo()
                    } else {
                        sendTTSMsg(NOT_STUDENT)
                    }
                }

                LoginState.STUDENT_LOGOUT -> {
                    if (it.cardType != CardType.StudentCard) {
                        sendTTSMsg(NOT_STUDENT)
                    } else if (!verifySamePerson(viewModel.studentModel, it)) {
                        sendTTSMsg(WRONG_STUDENT)
                    } else {
                        sendTTSMsg(WRITTING_CARD)
                        //写入最后签退日期
                        viewModel.writeMilesAndTime(
                            time = trainedTime,
                            miles = (distance / 100).toInt(),
                            date = formatDate(System.currentTimeMillis()),
                            todayTime = (if (viewModel.count % 60 > 30) viewModel.count / 60 + 1 else viewModel.count / 60) + todayPraticeTime
                        )
                    }
                }
            }
        })

        viewModel.writeCardLiveData.observe(this, Observer {
            //写卡成功
            if (it) {
                println("写卡结果：$it")
                if (validationType == VALIDATE_BOTH)
                    loginByFace()
                else {
                    sendTTSMsg(STUDENT_LOGOUT_SUCCESS)
                    setInfo()
                }
            } else {
                sendTTSMsg(WRITTING_CARD_FAIL)
            }
        })

        viewModel.loginState.observe(this, Observer {
            when (it) {
                LoginState.COACH_LOGIN,
                LoginState.COACH_LOGOUT,
                LoginState.STUDENT_LOGIN,
                LoginState.STUDENT_LOGOUT -> {
                    doLogin()
                }
                LoginState.TRAINING -> {
                    Log.d(TAG, "培训状态--正在培训")
                    sendTTSMsg(LOGIN_SUCCESS)
                    startTrain(0, 0, 0.0)
                }
                LoginState.STOPPED -> {
                    sendTTSMsg(STUDENT_LOGOUT_SUCCESS)
                    stopTrain()
                }
                else -> return@Observer
            }
        })

        viewModel.faceSearchLiveData.observe(this, Observer {
            if (it != null) {
                Log.d("FaceSearch", it.toString())
                when (viewModel.loginState.value) {
                    LoginState.COACH_LOGOUT -> if (it.usercode == viewModel.coachModel!!.id) {
                        sendTTSMsg(COACH_LOGOUT_SUCCESS)
                        setInfo()
                    } else
                        sendTTSMsg(WRONG_COACH)

                    LoginState.COACH_LOGIN -> {
                        CardInfo(
                            CardType.CommonCoachCard,
                            it.usercode,
                            it.userName,
                            it.idcard,
                            it.insName,
                            enumCarType(it.trainType.toHexInt()),
                            0,
                            if (it.subject == 2.toByte()) it.minSubHour else 0,
                            if (it.subject == 3.toByte()) it.minSubHour else 0,
                            if (it.subject == 2.toByte()) it.minSubMil else 0,
                            if (it.subject == 3.toByte()) it.minSubMil else 0,
                            0
                        ).also { cardInfo ->
                            if (verifyCarType(cardInfo)) {
                                viewModel.coachModel = cardInfo

                                sendTTSMsg(COACH_LOGIN_SUCCESS)
                                setInfo()
                            } else {
                                sendTTSMsg(COACH_CARTYPE_ERROR)
                            }
                        }
                    }
                    LoginState.STUDENT_LOGOUT -> if (it.usercode == viewModel.studentModel!!.id) {
                        sendTTSMsg(STUDENT_LOGOUT_SUCCESS)
                        setInfo()
                    } else
                        sendTTSMsg(WRONG_STUDENT)
                    LoginState.STUDENT_LOGIN -> {
                        sendTTSMsg(LOGIN_SUCCESS)
                        viewModel.studentModel = CardInfo(
                            CardType.StudentCard,
                            it.usercode,
                            it.userName,
                            it.idcard,
                            it.insName,
                            enumCarType(it.trainType.toHexInt()),
                            0,
                            if (it.subject == 2.toByte()) it.minSubHour else 0,
                            if (it.subject == 3.toByte()) it.minSubHour else 0,
                            if (it.subject == 2.toByte()) it.minSubMil else 0,
                            if (it.subject == 3.toByte()) it.minSubMil else 0,
                            0
                        )
                        todayPraticeTime = it.daySum
                        setInfo()
                    }
                    else -> return@Observer
                }
            } else {
                sendTTSMsg(FACE_FAIL)
            }
        })

        viewModel.matchFaceResultLiveData.observe(this, Observer {
            Log.d(
                TAG,
                "人脸识别结束: ${currentTimeWithSeconds()}, 返回码为：${it.error_code}, 返回结果为: ${it.error_msg}"
            )
            if (it.error_code == 0) {
                it.result.user_list?.run {
                    val person = get(0)
                    val groupId = person.group_id
                    val userId = person.user_id
                    val userInfo = person.user_info.split('_')
                    Log.d(TAG, "userInfo： ${person.user_info}")

                    when (viewModel.loginState.value) {
                        LoginState.COACH_LOGOUT -> if (userId == viewModel.coachModel!!.id) {
                            sendTTSMsg(COACH_LOGOUT_SUCCESS)
                            setInfo()
                        } else
                            sendTTSMsg(WRONG_COACH)

                        LoginState.COACH_LOGIN -> {
                            CardInfo(
                                CardType.CommonCoachCard,
                                userInfo[1],
                                userInfo[2],
                                userInfo[3],
                                userInfo[0],
                                enumCarType(userInfo[4].toHexInt()),
                                userInfo[5].toInt(),
                                userInfo[6].toInt(),
                                userInfo[7].toInt(),
                                userInfo[8].toInt(),
                                userInfo[9].toInt(),
                                userInfo[10].toInt()
                            ).also { cardInfo ->
                                if (verifyCarType(cardInfo)) {
                                    viewModel.coachModel = cardInfo

                                    sendTTSMsg(COACH_LOGIN_SUCCESS)
                                    setInfo()
                                } else {
                                    sendTTSMsg(COACH_CARTYPE_ERROR)
                                }
                            }
                        }
                        LoginState.STUDENT_LOGOUT -> if (userId == viewModel.studentModel!!.id) {
                            sendTTSMsg(STUDENT_LOGOUT_SUCCESS)
                            setInfo()
                        } else
                            sendTTSMsg(WRONG_STUDENT)
                        LoginState.STUDENT_LOGIN -> {
                            sendTTSMsg(LOGIN_SUCCESS)
                            viewModel.studentModel = CardInfo(
                                CardType.StudentCard,
                                userInfo[1],
                                userInfo[2],
                                userInfo[3],
                                userInfo[0],
                                enumCarType(userInfo[4].toHexInt()),
                                userInfo[5].toInt(),
                                userInfo[6].toInt(),
                                userInfo[7].toInt(),
                                userInfo[8].toInt(),
                                userInfo[9].toInt(),
                                userInfo[10].toInt()
                            )
                            setInfo()
                        }
                        else -> return@Observer
                    }
                }
            } else {
                sendTTSMsg(FACE_FAIL)
            }
        })

        viewModel.getRegisterInfo().observe(this, Observer {
            groupId = it.schoolNumber.ascii2String()
            Log.d(TAG, "驾校编号：${it.schoolNumber.ascii2String()}")
        })
    }

    /**
     * 验证最后签退日期是否为今天
     * @param lastCheckDate 最后签退日期
     */
    private fun isInSameDay(lastCheckDate: String) =
        formatDate(System.currentTimeMillis()) == lastCheckDate

    /**
     * 弹出是否继续学习对话框
     * @param studentModel 学员信息
     */
    private fun showContinueDialog(studentModel: SavedCardInfo) {
        AlertDialog.Builder(this)
            .setMessage("您有未完成的培训，是否继续学习？")
            .setNegativeButton("否") { _, _ ->
                trainedTime = studentModel.currentTrainTime
                viewModel.count = studentModel.currentCount
                distance = studentModel.currentTrainMiles

                viewModel.setState(LoginState.STUDENT_LOGOUT)
            }
            .setPositiveButton("是") { _, _ ->
                startTrain(
                    studentModel.currentCount,
                    studentModel.currentTrainTime,
                    studentModel.currentTrainMiles
                )
            }
            .setCancelable(false)
            .setOnCancelListener { DaoHelper.History.delete() }
            .show()
    }

    private fun formatTime(time: Int): String {
        val hour = DecimalFormat("00").format(time / 60)
        val minute = DecimalFormat("00").format(time % 60)
        return "${hour}小时${minute}分钟"
    }

    /**
     * 验证教练准教车型
     * @param cardInfo 教练信息
     */
    private fun verifyCarType(cardInfo: CardInfo): Boolean {
        return if (carMap.isNotEmpty() && carMap.containsKey(cardInfo.carType.name)) {
            val coachCarType = carMap[cardInfo.carType.name]
            Log.d(TAG, "车型为：${coachCarType}")
            if (coachCarType != null && !coachCarType.contains(carType)) {
                Log.d(TAG, "车型不符")
                false
            } else {
                Log.d(TAG, "车型匹配")
                true
            }
        } else {
            Log.d(TAG, "不进行车型匹配")
            true
        }
    }

    /**
     * 保存分钟学时信息
     */
    private fun insertRecord() {
        minuteDistance = if (minuteDistance == 0.0)
            distance
        else
            distance - minuteDistance

        RecordModel(
            viewModel.getRecordId(),
            0,
            viewModel.studentModel!!.id,
            viewModel.coachModel!!.id,
            viewModel.classId,
            if (viewModel.className == "第二部分") "1212130000" else "1213360000",
            1,
            (minuteMaxSpeed * 10).toInt(),
            minuteDistance.toInt() / 100,
            recordGnnsAddition()
        ).let {
            DaoHelper.Record.insertRecord(it)
            insertMessage(
                MessageModel(
                    sequenceNumber, ProtocolSend.SEND_PENETRATE_MESSAGE,
                    minuteRecord(it)
                )
            )
            minuteDistance = 0.0
            minuteMaxSpeed = 0.0
        }
    }

    /**
     * 页面填充学员和教练信息
     */
    private fun setInfo() {
        when (viewModel.loginState.value) {
            LoginState.COACH_LOGIN -> {
                setCoachLoginInfo()
            }
            LoginState.COACH_LOGOUT -> {
                clearCoachLoginInfo()
            }
            LoginState.STUDENT_LOGIN -> {
                setStudentLoginInfo()
            }
            LoginState.STUDENT_LOGOUT -> {
                clearStudentLoginInfo()
            }
            else -> Unit
        }
    }

    /**
     * 设置页面教练信息，将登录信息保存到数据库
     */
    private fun setCoachLoginInfo() {
        viewModel.coachModel?.let { info ->
            if (info.cardType == CardType.CommonCoachCard || info.cardType == CardType.PracticeCoachCard) {
                itemCoach.setName(info.name)
                    .setId(info.identification)
                    .setLoginStatus(true)
                viewModel.setState(LoginState.NOSTATE)

                CoachStateModel(
                    info.id,
                    info.identification,
                    info.carType.name,
                    LOGIN,
                    gnnsMessage()
                ).let {
                    viewModel.saveCoachLogin(it)
                    insertMessage(
                        MessageModel(
                            sequenceNumber,
                            ProtocolSend.SEND_PENETRATE_MESSAGE,
                            coachLogin(it)
                        )
                    )

                    captureInProgress(
                        EVENT_TYPE_COACH_LOGIN,
                        viewModel.coachModel,
                        viewModel.studentModel
                    )
                }

                viewModel.coachModel = info
            } else {
                sendTTSMsg(NOT_COACH)
            }
        }
    }

    /**
     * 设置页面学员信息，将登录信息保存到数据库
     */
    private fun setStudentLoginInfo() {
        viewModel.studentModel?.let { info ->
            if (info.cardType == CardType.StudentCard) {
                itemStudent.setName(info.name)
                    .setId(info.identification)
                    .setLoginStatus(true)

                viewModel.setState(LoginState.TRAINING)
                viewModel.coachModel?.let {
                    viewModel.classId = (System.currentTimeMillis() / 1000).toInt()

                    StudentStateModel(
                        viewModel.classId,
                        LOGIN,
                        info.id,
                        it.id,
                        if (viewModel.className == "第二部分") "1212130000" else "1213360000",
                        0,
                        0,
                        gnnsMessage()
                    ).let {
                        viewModel.saveStudentLogin(it)
                        insertMessage(
                            MessageModel(
                                sequenceNumber,
                                ProtocolSend.SEND_PENETRATE_MESSAGE,
                                studentLogin(it)
                            )
                        )

                        captureInProgress(
                            EVENT_TYPE_STUDENT_LOGIN,
                            viewModel.coachModel,
                            viewModel.studentModel
                        )
                    }
                }
                viewModel.studentModel = info
            } else {
                sendTTSMsg(NOT_STUDENT)
            }
        }
    }

    /**
     * 清空页面教练信息，将登出信息保存到数据库
     */
    private fun clearCoachLoginInfo() {
        captureInProgress(EVENT_TYPE_COACH_LOGOUT, viewModel.coachModel, viewModel.studentModel)

        viewModel.coachModel?.let { info ->
            itemCoach.setName("暂无")
                .setId("暂无")
                .setLoginStatus(false)
            viewModel.setState(LoginState.NOSTATE)
            viewModel.coachModel = null

            CoachStateModel(
                info.id,
                info.identification,
                info.carType.name,
                LOGOUT,
                gnnsMessage()
            ).let {
                viewModel.saveCoachLogin(it)
                insertMessage(
                    MessageModel(
                        sequenceNumber,
                        ProtocolSend.SEND_PENETRATE_MESSAGE,
                        coachLogout(it.coach_number)
                    )
                )
            }
        }
    }

    /**
     * 清空页面学员信息，将登出信息保存到数据库
     */
    private fun clearStudentLoginInfo() {
        captureInProgress(EVENT_TYPE_STUDENT_LOGOUT, viewModel.coachModel, viewModel.studentModel)

        viewModel.studentModel?.let { info ->
            itemStudent.setName("暂无")
                .setId("暂无")
                .setLoginStatus(false)
            viewModel.setState(LoginState.STOPPED)
            viewModel.studentModel = null
            viewModel.coachModel?.let {
                StudentStateModel(
                    viewModel.classId,
                    LOGOUT,
                    info.id,
                    it.id,
                    if (viewModel.className == "第二部分") "1212130000" else "1213360000",
                    (viewModel.count) / 60,
                    ((distance - startDistance) / 100).toInt(),
                    gnnsMessage()
                ).let {
                    viewModel.saveStudentLogin(it)
                    insertMessage(
                        MessageModel(
                            sequenceNumber,
                            ProtocolSend.SEND_PENETRATE_MESSAGE,
                            studentLogout(it)
                        )
                    )
                }
            }
        }
    }

    /**
     * 登录/登出
     */
    private fun doLogin() {
        if (isOnline.value == false && validationType != VALIDATE_CARD) {
            if (viewModel.loginState.value == LoginState.COACH_LOGOUT)
                showForceLogoutDialog(1)
            else if (viewModel.loginState.value == LoginState.STUDENT_LOGOUT)
                showForceLogoutDialog(0)
        } else {
            if (validationType != VALIDATE_FACE) {
                loginByCard()
            } else {
                loginByFace()
            }
        }
    }

    /**
     * 强制签退选项
     * @param type 0--学员，1--教练
     */
    private fun showForceLogoutDialog(type: Int) {
        AlertDialog.Builder(this)
            .setMessage("是否强制签退${if (type == 0) "学员" else "教练"}")
            .setPositiveButton(
                "是"
            ) { _, _ -> forceLogout(type) }
            .setNegativeButton("否") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    /**
     * 强制签退
     * @param type 0--学员，1--教练
     */
    private fun forceLogout(type: Int) {
        if (type == 0)
            clearStudentLoginInfo()
        else
            clearCoachLoginInfo()
    }

    /**
     * 人脸登录
     */
    private fun loginByFace() {

        when (viewModel.loginState.value) {
            LoginState.STUDENT_LOGIN,
            LoginState.STUDENT_LOGOUT -> {
                when (validationType) {
                    VALIDATE_BOTH -> sendTTSMsg(STUDENT_BOTH_FACE)
                    VALIDATE_FACE -> sendTTSMsg(STUDENT_FACE)
                    else -> Unit
                }
            }
            LoginState.COACH_LOGIN,
            LoginState.COACH_LOGOUT -> {
                when (validationType) {
                    VALIDATE_BOTH -> sendTTSMsg(COACH_BOTH_FACE)
                    VALIDATE_FACE -> sendTTSMsg(COACH_FACE)
                    else -> Unit
                }
            }
            else -> Unit
        }

        surfaceView.postDelayed({
            captureImageToLogin()
        }, 3000)
    }

    /**
     * 卡登录
     */
    private fun loginByCard() {
        viewModel.startCheckCard()
        when (viewModel.loginState.value) {
            LoginState.COACH_LOGIN -> {
                sendTTSMsg(COACH_CARD_LOGIN)
            }
            LoginState.COACH_LOGOUT -> {
                sendTTSMsg(COACH_CARD_LOGOUT)
            }
            LoginState.STUDENT_LOGIN -> {
                sendTTSMsg(STUDENT_CARD_LOGIN)
            }
            LoginState.STUDENT_LOGOUT -> {
                sendTTSMsg(STUDENT_CARD_LOGOUT)
            }
            else -> return
        }
    }

    /**
     * 开始培训
     * @param startCount 开始培训时的计时数
     * @param currentTime 开始培训时的已培训时长
     * @param currentDistance 开始培训时的已培训里程
     */
    private fun startTrain(startCount: Int, currentTime: Int, currentDistance: Double) {
        if (!isTraining) {
            itemCoach.setEnable(false)
            viewModel.startCountDown(startCount)
            isTraining = true
            currentClassItem = if (viewModel.className == "第二部分") 2 else 3

            trainedTime = currentTime
            distance = currentDistance
            minuteDistance = 0.0

            tvTotalTime.text = formatTime(trainedTime)
        }
    }

    /**
     * 结束培训
     */
    private fun stopTrain() {
        itemCoach.setEnable(true)
        viewModel.stopCountDown()
        DaoHelper.History.delete()
        isTraining = false
        currentClassItem = 0

        trainedTime = 0
        distance = 0.0
        startDistance = 0.0
        timeAndMilesModel.postValue(TimeAndMilesModel(0, 0, 0, 0))
    }

    /**
     * 验证是否为同一人刷卡
     * @param previous 登录刷卡信息
     * @param current 登出刷卡信息
     */
    private fun verifySamePerson(previous: CardInfo?, current: CardInfo?) =
        previous != null && current != null && (previous.id == current.id)

    /**
     * 初始化摄像头
     */
    private fun startCamera() {

        mCamera = Camera.open()
        if (mCamera != null) {
            mCamera!!.parameters.setPictureSize(640, 480)
            surfaceView.removeAllViews()
            surfaceView.addView(CameraPreview(this, mCamera))
        } else {
            toast("相机开启失败，请退出重试")
        }
    }

    /**
     * 登录登出时拍照验证(人脸识别)
     */
    private fun captureImageToLogin() {
        val photoId = getPhotoId()
        val file = File(externalMediaDirs.first(), "$photoId.jpg")

        Log.d(TAG, "开始拍照: ${currentTimeWithSeconds()}")
        mCamera?.takePicture(null, null){ data, _ ->

            viewModel.savePicture(file, data)

            val str = Base64.encodeToString(
                compressImage(data, null, false),
                Base64.DEFAULT
            )
            Log.d(TAG, "加密串大小: ${str.length / 1024}KB")
            Log.d(TAG, "BASE64加密结束: ${currentTimeWithSeconds()}")

            val group_id = groupId
            var type = 1
            val numberNeed = when (validationType) {
                VALIDATE_BOTH -> true
                else -> false
            }
            when (viewModel.loginState.value) {
                LoginState.STUDENT_LOGIN,
                LoginState.STUDENT_LOGOUT -> {
                    type = 1
                }
                LoginState.COACH_LOGIN,
                LoginState.COACH_LOGOUT -> {
                    type = 2
                }
            }
            viewModel.searchFace(str.replaceBlank(), group_id, type, numberNeed)
        }
    }

    /**
     * 签到签退及过程中拍照
     * @param eventType 拍摄类型
     * @param coachModel 缓存的教练信息，防止签退后被清空
     * @param studentModel 缓存的学员信息，防止签退后被清空
     */
    private fun captureInProgress(eventType: Byte, coachModel: CardInfo?, studentModel: CardInfo?) {

        when (eventType) {
            EVENT_TYPE_STUDENT_LOGIN -> Log.d(TAG, "学员登录拍照")
            EVENT_TYPE_STUDENT_LOGOUT -> Log.d(TAG, "学员登出拍照")
            EVENT_TYPE_COACH_LOGIN -> Log.d(TAG, "教练登录拍照")
            EVENT_TYPE_COACH_LOGOUT -> Log.d(TAG, "教练登出拍照")
            EVENT_TYPE_ON_TIME -> Log.d(TAG, "过程照片")
            else -> Unit
        }

        val photoId = getPhotoId()
        val file = File(externalMediaDirs.first(), "$photoId.jpg")

        //学员/教练id
        val id = when (eventType) {
            EVENT_TYPE_COACH_LOGIN,
            EVENT_TYPE_COACH_LOGOUT -> coachModel!!.id.toByteArray()
            EVENT_TYPE_STUDENT_LOGIN,
            EVENT_TYPE_STUDENT_LOGOUT,
            EVENT_TYPE_ON_TIME -> studentModel!!.id.toByteArray()
            else -> ByteArray(16)
        }

        mCamera?.takePicture(null, null){ data, _ ->

            viewModel.savePicture(file, data)

            Log.d(TAG, "照片长度：${file.length()}")
            val compressedImage =
                compressImage(data, getPrintInfo(eventType, coachModel, studentModel))
            Log.d(TAG, "压缩后长度：${compressedImage.size}")
            var totalPackages = compressedImage.size / PHOTO_PACKAGE_SIZE
            if (compressedImage.size % PHOTO_PACKAGE_SIZE != 0)
                totalPackages++

            PictureInitModel(
                eventType = eventType,
                pictureId = photoId.toByteArray(),
                studentNumber = id,
                totalPackages = totalPackages.toShort(),
                imageSize = file.length().toInt(),
                classId = viewModel.classId,
                gnnsData = recordGnnsAddition()
            ).let {
                Log.d(TAG, "拍照结束，存入数据库")
                DaoHelper.Photo.insert(it)
                insertMessage(
                    MessageModel(
                        sequenceNumber,
                        ProtocolSend.SEND_PENETRATE_MESSAGE,
                        initPicture(it)
                    )
                )
                viewModel.insertPhotoPackages(
                    compressedImage,
                    photoId.toByteArray()
                )
            }
        }
    }

    /**
     * 生成照片水印信息
     */
    private fun getPrintInfo(eventType: Byte, coachModel: CardInfo?, studentModel: CardInfo?):
            PhotoPrintInfo {
        Log.d(TAG, "教练：$coachModel")
        Log.d(TAG, "学员：$studentModel")
        val info = PhotoPrintInfo()
        info.coachName = "教练：${coachModel!!.name}"
        when (eventType) {
            EVENT_TYPE_COACH_LOGIN,
            EVENT_TYPE_COACH_LOGOUT -> {
                info.identify = coachModel.identification
                info.studentName = ""
            }
            else -> {
                info.identify = studentModel!!.identification
                info.studentName = "学员：${studentModel.name}"
            }
        }
        registerInfo?.schoolNumber?.let {
            info.deviceID = String(it)
        }
        info.datetime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINESE).format(Date())
        currentLocation?.run {
            info.location = "经纬：${String.format("%3.5fE-%3.5fN", longitude, latitude)}"
            info.speed = String.format("车速:%dKm/h", satellite_speed.toInt())
        }
        info.carNum = carNum
        //此处应该是驾校名称
        viewModel.coachModel?.run {
            info.schoolName = ""
        }
        return info
    }


    /**
     * 验证是否拥有所申请权限
     */
    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 生成照片id
     */
    private fun getPhotoId(): String =
        formatTimeHms(System.currentTimeMillis()).substring(2 until 12)

    override fun onDestroy() {
        viewModel.stopCountDown()
        viewModel.stopCheckCard()

        if (mCamera != null) {
            mCamera!!.stopPreview()
            mCamera!!.release()
            mCamera = null
        }

        super.onDestroy()
    }
}