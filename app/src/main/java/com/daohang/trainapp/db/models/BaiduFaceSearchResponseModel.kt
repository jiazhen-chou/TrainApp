package com.daohang.trainapp.db.models

data class BaiduFaceSearchResponseModel(
    val error_code: Int,
    val error_msg: String,
    val log_id: String,
    val timestamp: Int,
    val cached: Int,
    val result: BaiduFaceResult
)

data class BaiduFaceResult(
    val face_token: String?,
    val user_list: List<BaiduFaceUserList>?
)

data class BaiduFaceUserList(
    val group_id: String,
    val user_id: String,
    val user_info: String,
    val score: Float
)