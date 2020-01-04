package com.daohang.trainapp.network.requestModel

data class MatchFaceRequestModel(
    val image: String,
    val image_type: String = "BASE64"
)