package com.daohang.trainapp.network.api

import com.daohang.trainapp.db.models.GeoFenceModel
import com.daohang.trainapp.db.models.VersionInfo
import com.daohang.trainapp.network.ApiResult
import com.daohang.trainapp.network.CommonApiResult
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProjectApi {

    /**
     * 获取电子围栏
     */
    @GET("getregions/{imei}")
    suspend fun getGeoFence(@Path("imei") imei: String): Response<ApiResult<MutableList<GeoFenceModel>>>

    @GET("version/{code}/2")
    suspend fun getNewVersion(@Path("code") code: String): Response<CommonApiResult<VersionInfo>>
}