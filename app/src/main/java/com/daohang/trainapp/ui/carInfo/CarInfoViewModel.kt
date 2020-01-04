package com.daohang.trainapp.ui.carInfo

import android.app.Application
import com.daohang.trainapp.ui.BaseViewModel

class CarInfoViewModel(application: Application): BaseViewModel(application){

    fun getCarInfo() =
        database.preferenceDao().getPreferenceLiveData()

}