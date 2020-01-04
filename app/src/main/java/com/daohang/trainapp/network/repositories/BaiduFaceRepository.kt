package com.daohang.trainapp.network.repositories

import com.daohang.trainapp.db.models.BaiduAccessTokenModel
import com.daohang.trainapp.db.models.BaiduFaceSearchResponseModel
import com.daohang.trainapp.db.models.BaiduMatchFaceResponseModel
import com.daohang.trainapp.network.api.BaiduApi
import com.daohang.trainapp.network.requestModel.FaceSearchRequestModel
import com.daohang.trainapp.network.requestModel.MatchFaceRequestModel

class BaiduFaceRepository(private val api: BaiduApi) {

    suspend fun getAccessToken(): BaiduAccessTokenModel? = api.getAccessToken().body()

    suspend fun matchFace(
        token: String,
        body: Array<MatchFaceRequestModel>
    ): BaiduMatchFaceResponseModel? = api.matchFace(token, body).body()

    suspend fun searchFace(
        token: String,
        body: FaceSearchRequestModel
    ): BaiduFaceSearchResponseModel? = api.searchFace(token, body).body()

}