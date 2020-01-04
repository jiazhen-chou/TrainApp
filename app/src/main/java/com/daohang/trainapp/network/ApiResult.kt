package com.daohang.trainapp.network

data class ApiResult<T: Any>(val code: Int, val msg: String?, val data: T)

data class CommonApiResult<T: Any>(val errorcode: Int, val message: String?, val data: T?)