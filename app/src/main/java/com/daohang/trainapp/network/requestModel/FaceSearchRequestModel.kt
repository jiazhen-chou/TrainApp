package com.daohang.trainapp.network.requestModel

data class FaceSearchRequestModel(
    val image: String,
    val image_type: String = "BASE64",
    val group_id_list: String
)