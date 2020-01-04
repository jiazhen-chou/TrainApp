package com.daohang.trainapp.ui.carSetting

import android.app.Application
import com.daohang.trainapp.db.DaoHelper
import com.daohang.trainapp.db.models.PreferenceModel
import com.daohang.trainapp.db.models.Province
import com.daohang.trainapp.ui.BaseViewModel
import com.daohang.trainapp.utils.readJsonFromFile

class CarSettingViewModel(application: Application) : BaseViewModel(application) {

    private val provinceList: List<Province> =
        application.readJsonFromFile<Array<Province>>("city_code.json").toList()

    fun getProvinceStringList(): MutableList<String> {
        val list = mutableListOf<String>()

        for (p in provinceList) {
            list.add(p.name)
        }

        return list
    }

    fun getCityStringList(index: Int): MutableList<String> {
        val p = provinceList[index]
        val list = mutableListOf<String>()

        for (c in p.city) {
            list.add(c.name)
        }

        return list
    }

    fun savePreference(preferenceModel: PreferenceModel){
        preferenceModel.let {
//            DaoHelper.Preference.deletePreference()
            DaoHelper.Preference.insertPreference(it)
        }
    }
}