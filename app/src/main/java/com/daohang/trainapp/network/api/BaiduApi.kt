package com.daohang.trainapp.network.api

import com.daohang.trainapp.db.models.BaiduAccessTokenModel
import com.daohang.trainapp.db.models.BaiduFaceSearchResponseModel
import com.daohang.trainapp.db.models.BaiduMatchFaceResponseModel
import com.daohang.trainapp.network.requestModel.FaceSearchRequestModel
import com.daohang.trainapp.network.requestModel.MatchFaceRequestModel
import com.daohang.trainapp.constants.BAIDU_AK
import com.daohang.trainapp.constants.BAIDU_GRANT_TYPE
import com.daohang.trainapp.constants.BAIDU_SK
import retrofit2.Response
import retrofit2.http.*

interface BaiduApi {

    /**
     * 获取access_token
     */
    @GET("oauth/2.0/token?grant_type=$BAIDU_GRANT_TYPE&client_id=$BAIDU_AK&client_secret=$BAIDU_SK")
    suspend fun getAccessToken(): Response<BaiduAccessTokenModel>


    /**
     * 人脸比对(需传两张图片)
     */
    @Headers("Content-Type:application/json")
    @POST("rest/2.0/face/v3/match")
    suspend fun matchFace(@Query("access_token") token: String, @Body body: Array<MatchFaceRequestModel>): Response<BaiduMatchFaceResponseModel>


//    @Headers("Content-Type:application/json")
    @POST("rest/2.0/face/v3/search")
    suspend fun searchFace(@Query("access_token") token: String, @Body body: FaceSearchRequestModel): Response<BaiduFaceSearchResponseModel>
}