package com.daohang.trainapp.ui.trainParameter

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.daohang.trainapp.db.models.TrainPreferenceModel
import com.daohang.trainapp.ui.BaseViewModel
import kotlinx.coroutines.launch

class TrainParameterViewModel(application: Application): BaseViewModel(application){

    val trainPreferenceLiveData: LiveData<TrainPreferenceModel> = database.trainPreferenceDao().getTrainPreferenceLiveData()
//    lateinit var trainPreferenceLiveData: LiveData<TrainPreferenceModel>


    fun fetchProjectTrainPreference() {
//        scope.launch {
//            trainPreferenceLiveData.postValue(projectRepository.getProjectTrainPreference(database.preferenceDao().getPreference()!!.projectId))
//        }
//        trainPreferenceLiveData = database.trainPreferenceDao().getTrainPreferenceLiveData()
    }
}