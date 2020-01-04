package com.daohang.trainapp.network

import com.daohang.trainapp.network.api.Api
import com.daohang.trainapp.network.api.BaiduApi
import com.daohang.trainapp.network.api.DownloadApi
import com.daohang.trainapp.network.api.ProjectApi
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

object ApiFactory {

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60 * 1000,TimeUnit.MILLISECONDS)
        .readTimeout(60 * 1000,TimeUnit.MILLISECONDS)
        .writeTimeout(60 * 1000,TimeUnit.MILLISECONDS)
        .protocols(Collections.singletonList(Protocol.HTTP_1_1))
        .retryOnConnectionFailure(false)
        .connectionPool(ConnectionPool(0,1,TimeUnit.NANOSECONDS))
        .build()

    private fun retrofit(): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("http://114.116.74.148:8088/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    var api: Api = retrofit().create(
        Api::class.java)

    private fun baiduRetrofit(): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://aip.baidubce.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val baiduApi = baiduRetrofit().create(BaiduApi::class.java)

    fun projectRetrofit(ip: String, port: Int): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("http://$ip:$port/driverTrain/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    var projectApi: ProjectApi? = null

    fun downloadRetrofit(ip: String, port: Int): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("http://$ip:$port")
        .build()

    var downloadApi: DownloadApi? = null
}