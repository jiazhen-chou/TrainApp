package com.daohang.trainapp.network.api

import com.daohang.trainapp.db.models.PreferenceModel
import com.daohang.trainapp.db.models.TrainPreferenceModel
import com.daohang.trainapp.network.ApiResult
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface Api {

    @POST("appuser/getAllProjectInfo")
    suspend fun getProjects(): Response<ApiResult<MutableList<PreferenceModel>>>

    @POST("appuser/getProjectParameter")
    suspend fun getProjectInfo(@Query("projectId") id: Int): Response<ApiResult<TrainPreferenceModel>>

}