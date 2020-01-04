package com.daohang.trainapp.network.repositories

import com.daohang.trainapp.db.models.FaceSearchRequestModel
import com.daohang.trainapp.db.models.FaceSearchResponseModel
import com.daohang.trainapp.db.models.GeoFenceModel
import com.daohang.trainapp.db.models.VersionInfo
import com.daohang.trainapp.network.BaseRepository
import com.daohang.trainapp.network.api.ProjectApi

class CommonRepository(private val api: ProjectApi) : BaseRepository() {

    /**
     * 获取电子围栏
     * @param imei 设备IMEI号
     */
    suspend fun getGeoFence(imei: String): MutableList<GeoFenceModel>? {
        safeApiCall(
            call = { api.getGeoFence(imei) },
            errorMessage = "获取电子围栏失败"
        )?.let {
            return it.data
        }
        return mutableListOf()
    }

    /**
     * 获取最新版本号
     * @param code 升级码
     */
    suspend fun getNewVersion(code: String): VersionInfo? {
        safeApiCall(
            call = { api.getNewVersion(code) },
            errorMessage = "获取版本信息失败"
        )?.let {
            return it.data
        }
        return VersionInfo("", -1)
    }

    /**
     * 人脸识别
     * @param model 图片信息
     */
    suspend fun faceSearch(model: FaceSearchRequestModel): FaceSearchResponseModel? {
        safeApiCall(
            call = { api.searchFace(model) },
            errorMessage = "人脸验证失败"
        )?.let {
            return it.data
        }
        return null
    }
}