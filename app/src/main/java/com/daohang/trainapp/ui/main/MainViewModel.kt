package com.daohang.trainapp.ui.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.daohang.trainapp.db.DaoHelper
import com.daohang.trainapp.db.models.PreferenceModel
import com.daohang.trainapp.db.models.VersionInfo
import com.daohang.trainapp.network.ApiFactory
import com.daohang.trainapp.network.api.ProjectApi
import com.daohang.trainapp.network.repositories.CommonRepository
import com.daohang.trainapp.ui.BaseViewModel
import kotlinx.coroutines.launch
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainViewModel(application: Application) : BaseViewModel(application) {

    var isDownloading = false

    var code: String = ""

    var domain: String = ""

    var port: Int = 0

    var progress = MutableLiveData<Int>()

    lateinit var commonRepository: CommonRepository

    var newVersionInfo = MutableLiveData<VersionInfo?>()

    fun getPreferenceModel() = DaoHelper.Preference.getPreferenceLiveData()

    val preference = MutableLiveData<PreferenceModel>()

    fun getCertification() = DaoHelper.Register.getRegisterInfo()

    /**
     * 检查是否有未签退的培训
     */
    fun getUnCompleteClass() = DaoHelper.History.getUnCompleteInfo()

    fun getUnCompleteCount() = DaoHelper.History.getUnCompleteCount()

    val trainPreferenceEnable = MutableLiveData<Boolean>(false)

    /**
     * 从数据库读取培训参数
     */
    fun getTrainParameter() {
        scope.launch {
            val model = DaoHelper.TrainPreference.getTrainPreference()
            if (model != null)
                trainPreferenceEnable.postValue(true)
            else
                fetchTrainParameter()
        }
    }

    /**
     * 拉取培训参数
     */
    private fun fetchTrainParameter() {
        scope.launch {
            DaoHelper.Preference.getPreference()?.let {
                projectRepository.getProjectTrainPreference(it.projectId)?.let {
                    database.trainPreferenceDao().deleteTrainPreference()
                    database.trainPreferenceDao().insertTrainPreference(it)
                    trainPreferenceEnable.postValue(true)
                }
            }
        }
    }

    /**
     * 实例化接口
     */
    fun initProjectApi(domain: String, port: Int) {
        ApiFactory.projectApi = ApiFactory.projectRetrofit(domain, port).create(
            ProjectApi::class.java
        )
        println("人脸识别接口地址：$domain:$port")
        this.domain = domain
        this.port = port
    }

    /**
     * 获取最新版本
     */
    fun fetchNewVersionCode() {
        if (code.isNotEmpty())
            ApiFactory.projectApi?.run {
                commonRepository = CommonRepository(this)
                scope.launch {
                    newVersionInfo.postValue(commonRepository.getNewVersion(code))
                }
            }
    }

    /**
     * 下载apk
     * @param version 最新版本号
     * @param fileName 文件保存位置
     */
    fun downloadNewApk(url: String, dir: String, fileName: String) {

        val file = File(dir)
        if (!file.exists())
            file.mkdirs()

//        code = "wnjp"
        val request = Request.Builder()
            .url(url)
            .build()
        ApiFactory.okHttpClient.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                println("下载失败")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body()?.run {
                    val file = File(fileName)
                    val maxSize = 34 * 1024 * 1024
                    byteStream().use {
                        isDownloading = true
                        progress.postValue(0)
                        val outStream = FileOutputStream(file)
                        var buff = ByteArray(1024)
                        var len = 0
                        var tempProgress = 0
                        var total: Long = 0
                        //is.read(byte[] b) : 在输入流中读取一定量的字节,并将其存储在b中，以整数形式返回实际读取的字节数。
                        while (true) {
                            len = it.read(buff)
                            if (len != -1) {
                                outStream.write(buff, 0, len)
                                total += len
                                tempProgress = (total * 100 / maxSize).toInt()
                                if (tempProgress != progress.value) {
                                    progress.postValue(tempProgress)
                                }
                            } else {
                                break
                            }
                        }
                        isDownloading = false
                        outStream.flush()
                        progress.postValue(100)
                        println("下载成功")
                    }
                }
            }

        })
    }
}