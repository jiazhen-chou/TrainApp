package com.daohang.trainapp.ui.parameterSetting

import android.app.Application
import com.daohang.trainapp.db.DaoHelper
import com.daohang.trainapp.ui.BaseViewModel
import kotlinx.coroutines.launch

class ParameterSettingViewModel(application: Application) : BaseViewModel(application){

    fun factoryReset(){
        scope.launch {
            DaoHelper.deleteAll()
        }
    }
}