package com.daohang.trainapp.network.repositories

import com.daohang.trainapp.db.models.PreferenceModel
import com.daohang.trainapp.db.models.TrainPreferenceModel
import com.daohang.trainapp.network.api.Api
import com.daohang.trainapp.network.BaseRepository

class ProjectRepository(private val api: Api) : BaseRepository() {

    suspend fun getProjects(): MutableList<PreferenceModel>? {

        safeApiCall(
            call = { api.getProjects() },
            errorMessage = "项目列表请求出错"
        )?.let {
            return it.data
        }
        return mutableListOf()
    }

    suspend fun getProjectTrainPreference(projectId: Int): TrainPreferenceModel? {

        safeApiCall(
            call = { api.getProjectInfo(projectId) },
            errorMessage = "培训参数请求出错"
        )?.let {
            return it.data
        }
        return null
    }
}