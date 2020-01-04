package com.daohang.trainapp.ui.projectSelect

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.daohang.trainapp.db.models.PreferenceModel
import com.daohang.trainapp.ui.BaseViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ProjectSelectViewModel(application: Application) : BaseViewModel(application) {


    val projectsLiveData = MutableLiveData<MutableList<PreferenceModel>>()

    fun fetchProjects() {
        scope.launch {
            projectsLiveData.postValue(projectRepository.getProjects())
        }
    }

    fun cancelAllRequests() = coroutineContext.cancel()
}