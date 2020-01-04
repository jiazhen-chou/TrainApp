package com.daohang.trainapp.db.models

data class BaiduMatchFaceResponseModel(
    val score: Float,
    val face_list: List<FaceList>?,
    val error_code: Int,
    val error_msg: String?
)

data class FaceList(
    val face_token: String
)