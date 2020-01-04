package com.daohang.trainapp.ui.train

import android.app.Application
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import com.daohang.trainapp.MyApplication
import com.daohang.trainapp.constants.PHOTO_PACKAGE_SIZE
import com.daohang.trainapp.constants.ProtocolSend
import com.daohang.trainapp.constants.RECORD_ID
import com.daohang.trainapp.constants.RECORD_TIME
import com.daohang.trainapp.db.DaoHelper
import com.daohang.trainapp.db.models.*
import com.daohang.trainapp.enums.LoginState
import com.daohang.trainapp.network.ApiFactory
import com.daohang.trainapp.network.repositories.BaiduFaceRepository
import com.daohang.trainapp.network.repositories.CommonRepository
import com.daohang.trainapp.network.requestModel.FaceSearchRequestModel
import com.daohang.trainapp.services.*
import com.daohang.trainapp.ui.BaseViewModel
import com.daohang.trainapp.ui.registerInfo
import com.daohang.trainapp.utils.*
import com.yz.lz.modulapi.CardInfo
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.Executors
import kotlin.concurrent.thread

class TrainViewModel(application: Application) : BaseViewModel(application) {

    lateinit var commonRepository: CommonRepository

    private val baiduFaceRepository = BaiduFaceRepository(ApiFactory.baiduApi)

    //百度人脸识别结果
    val matchFaceResultLiveData = MutableLiveData<BaiduFaceSearchResponseModel>()
    //培训参数
    val trainPreferenceLiveData = DaoHelper.TrainPreference.getTrainPreferenceLiveData()
    //写卡结果
    val writeCardLiveData = MutableLiveData<Boolean>()
    //计时器
    private var countDownTimerTask: TimerTask? = null
    val countDown: MutableLiveData<String> = MutableLiveData()
    var count = 0

    //当前培训登录教练信息
    var coachModel: CardInfo? = null

    //当前培训登录学员信息
    var studentModel: CardInfo? = null

    //培训状态
    var loginState = MutableLiveData<LoginState>()

    //卡片检测结果
    var checkCardLiveData = MutableLiveData<Boolean>()
    //卡片信息
    var cardInfoLiveData = MutableLiveData<CardInfo>()

    //卡片检测定时任务
    var checkCardTask: TimerTask? = null

    val executor = Executors.newSingleThreadExecutor()

    //课堂id
    var classId = 0
    //课程名
    var className = ""

    /**
     * 开始计时
     */
    fun startCountDown(startCount: Int) {
        count = startCount
        countDownTimerTask?.cancel()
        countDownTimerTask = object : TimerTask() {
            override fun run() {
                countDown.postValue(generateCountDown(count++))
            }
        }
        Timer().schedule(countDownTimerTask, 0, 1000)
    }

    /**
     * 保存临时状态
     */
    fun saveRecord(distance: Double, time: Int) {
        coachModel?.run {
            DaoHelper.History.insert(
                SavedCardInfo(
                    id = classId + 1,
                    className = className,
                    type = 1,
                    studentNumber = id,
                    name = name,
                    identification = identification,
                    company = company,
                    carType = carType.byte
                )
            )
        }
        studentModel?.run {
            DaoHelper.History.insert(
                SavedCardInfo(
                    id = classId,
                    className = className,
                    type = 0,
                    studentNumber = id,
                    name = name,
                    identification = identification,
                    company = company,
                    carType = carType.byte,
                    currentCount = count,
                    currentTrainTime = time,
                    //暂时设置为0
                    currentTrainMiles = distance
                )
            )
        }
    }

    /**
     * 生成记录编号
     */
    fun getRecordId(): ByteArray {
        getApplication<MyApplication>().getRecordIdSp().run {
            val buffer = mutableListOf<Byte>()
            if (registerInfo != null)
                buffer.addAll(registerInfo!!.terminalNumber.toList())
            else
                buffer.addAll(ByteArray(16).toList())

            val date = formatTimeHms(System.currentTimeMillis()).substring(2 until 8)
            buffer.addAll(date.toByteArray().toList())

            var recordId = getInt(RECORD_ID, 1)
            if (date == getString(RECORD_TIME, "000000")) {
                buffer.addAll(recordId.toByteArray4().toList())
                edit(commit = true) {
                    putInt(RECORD_ID, recordId + 1)
                }
            } else {
                buffer.addAll(1.toByteArray4().toList())
                edit(commit = true) {
                    putInt(RECORD_ID, 2)
                    putString(RECORD_TIME, date)
                }
            }
            return buffer.toByteArray()
        }
    }

    /**
     * 检查是否有未签退的培训
     */
    fun getUnCompleteClass() = DaoHelper.History.getUnCompleteInfo()

    /**
     * 停止计时
     */
    fun stopCountDown() {
        countDownTimerTask?.cancel()
    }


    /**
     * 保存教练登签记录
     */
    fun saveCoachLogin(model: CoachStateModel) {
        thread {
            database.coachStateDao().insertCoachLogin(model)
        }
    }

    /**
     * 保存学员登签记录
     */
    fun saveStudentLogin(model: StudentStateModel) {
        thread {
            database.studentStateDao().insertStudentLogin(model)
        }
    }

    /**
     * 开始检测卡
     */
    fun startCheckCard() {
        checkCardTask = object : TimerTask() {
            override fun run() {
                val result = checkCard()
                if (result) {
                    checkCardLiveData.postValue(result)
                    readCardInfo()?.let {
                        cardInfoLiveData.postValue(it)
                        cancel()
                    }
                }
            }
        }
        Timer().schedule(checkCardTask, 0, 500)
    }

    fun stopCheckCard() = checkCardTask?.cancel()

    /**
     * 更改培训状态
     */
    fun setState(state: LoginState) {
        loginState.postValue(state)
    }

    /**
     * 获取注册信息
     */
    fun getRegisterInfo() = DaoHelper.Register.getRegisterInfo()

    /**
     * 人脸识别
     */
    fun matchFace(picture: String, groupId: String) {
        scope.launch {
            baiduFaceRepository.getAccessToken()?.let {
                println("Token获取成功: ${currentTimeWithSeconds()}")
                matchFaceResultLiveData.postValue(
                    baiduFaceRepository.searchFace(
                        it.access_token,
                        FaceSearchRequestModel(picture, group_id_list = groupId)
                    )
//                    baiduFaceRepository.searchFace(it.access_token, FaceSearchRequestModel(picture, group_id_list = groupId))
                )
            }
        }
    }

    /**
     * 写卡
     * @param className 课程名（第二部分，第三部分）
     * @param time 已培训学时
     * @param miles 已培训里程
     */
    fun writeMilesAndTime(time: Int, miles: Int) {
        thread {
            val data = ByteArray(8)
            System.arraycopy(time.toByteArray4(), 0, data, 0, 4)
            System.arraycopy(miles.toByteArray4(), 0, data, 4, 4)
            writeCardLiveData.postValue(
                if (className == "第二部分")
                    writeSubjectTwo(data)
                else
                    writeSubjectThree(data)
            )
        }
    }

    /**
     * 写入最后一次签退日期
     */
    fun writeLastDate(date: String) {
        thread {
            writeLastExitDate(date.toBcd())
        }
    }

    /**
     * 写入今日培训时长
     */
    fun writePraticeTimeToday(time: Int){
        thread {
            writeToadyPraticeTime(time.toByteArray4())
        }
    }

    /**
     * 获取电子围栏
     */
    fun fetchGeoFence() {
        ApiFactory.projectApi?.run {
            commonRepository = CommonRepository(this)
            scope.launch {
                commonRepository.getGeoFence(MyApplication.IMEI)?.run {
                    if (isNotEmpty())
                        polygons.clear()

                    forEach {
                        //                        createGeoFence(separatePolygonString(it.polygon), it.subject.toString())
                        polygons[it.subject] = separatePolygonString(it.polygon)
                    }
                }
            }
        }
    }

    /**
     * 插入照片数据包
     */
    fun insertPhotoPackages(imageArray: ByteArray, photoId: ByteArray) =
        thread {
            val imageBytes =
                extenededProtocol(ProtocolSend.SEND_PICTURE, photoId + imageArray)
            var packagesCount = imageBytes.size / PHOTO_PACKAGE_SIZE
            if (imageBytes.size % PHOTO_PACKAGE_SIZE != 0)
                packagesCount++

            for (index in 0 until packagesCount) {
                val startIndex = PHOTO_PACKAGE_SIZE * index
                val endIndex =
                    if (index == packagesCount - 1) imageBytes.size else PHOTO_PACKAGE_SIZE * (index + 1)
                insertMessage(
                    MessageModel(
                        sequenceNumber,
                        ProtocolSend.SEND_PENETRATE_MESSAGE,
                        separatePhoto(
                            imageBytes.sliceArray(startIndex until endIndex),
                            packagesCount,
                            index + 1
                        )
                    )
                )
            }
        }
}