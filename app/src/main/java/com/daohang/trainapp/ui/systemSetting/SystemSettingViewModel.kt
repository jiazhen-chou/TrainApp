package com.daohang.trainapp.ui.systemSetting

import android.app.Application
import com.daohang.trainapp.ui.BaseViewModel

class SystemSettingViewModel(application: Application): BaseViewModel(application){

    fun getPreferenceModel() =
        database.preferenceDao().getPreferenceLiveData()

}