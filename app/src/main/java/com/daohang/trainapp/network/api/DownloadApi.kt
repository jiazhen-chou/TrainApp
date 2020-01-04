package com.daohang.trainapp.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface DownloadApi {

    @GET("apk/{version}/{code}/2")
    suspend fun downloadApk(@Path("version") version: Int, @Path("code") code: String): Call<ResponseBody>

}