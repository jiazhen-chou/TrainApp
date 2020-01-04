package com.daohang.trainapp.ui.selfcheck

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.daohang.trainapp.constants.*
import com.daohang.trainapp.ui.BaseViewModel
import com.daohang.trainapp.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

class SelfCheckViewModel(application: Application) : BaseViewModel(application) {

    var checkResult = MutableLiveData<Pair<Int, Boolean>>()

    /**
     * 开机自检
     */
    fun checkState() = thread {
        runBlocking {
            checkResult.postValue(Pair(RFID_CHECK_NUM, checkRfid()))
            delay(500)
            checkResult.postValue(Pair(GPS_CHECK_NUM, checkGps()))
            delay(500)
            checkResult.postValue(Pair(CAMERA_CHECK_NUM, checkCamera()))
            delay(500)
            checkResult.postValue(Pair(NETWORK_CHECK_NUM, checkNetwork()))
            delay(500)
            checkResult.postValue(Pair(OBD_CHECK_NUM, checkObd()))
            //此处延迟500ms，保证最后一项检测结果可以显示
            delay(500)
            checkResult.postValue(Pair(COMPLETE_CHECK, true))
        }
    }

    val preferenceCount = database.preferenceDao().getPreferenceCountLiveData()

    fun getTrainParameter() {
        scope.launch {
            database.preferenceDao().getPreference()?.let {
                projectRepository.getProjectTrainPreference(it.projectId)?.let {
                    database.trainPreferenceDao().deleteTrainPreference()
                    database.trainPreferenceDao().insertTrainPreference(it)
                }
            }
        }
    }
}