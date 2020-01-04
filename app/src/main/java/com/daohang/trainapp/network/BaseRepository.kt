package com.daohang.trainapp.network

import android.util.Log
import com.daohang.trainapp.db.TrainDatabase
import retrofit2.Response
import java.io.IOException
import java.lang.Exception

/**
 * 网络请求基类
 */
abstract class BaseRepository {
    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>, errorMessage: String): T? {

        val result : Result<T> = safeApiResult(call,errorMessage)
        var data : T? = null

        when(result) {
            is Result.Success ->
                data = result.data
            is Result.Error -> {
                Log.d("1.DataRepository", "$errorMessage & Exception - ${result.exception}")
            }
        }


        return data

    }

    private suspend fun <T: Any> safeApiResult(call: suspend ()-> Response<T>, errorMessage: String) : Result<T>{
        try {
            val response = call.invoke()
            if(response.isSuccessful)
                return Result.Success(response.body()!!)
//            else {
//                val data = getDataFromDB<T>()
//                data?.let {
//                    return Result.Success(data)
//                }
//            }
        } catch (e: Exception){
            println("Exception catched: ${e.message}")
        }
        return Result.Error(IOException("Error Occurred during getting safe Api result, Custom ERROR - $errorMessage"))
    }

//    abstract fun <T : Any> getDataFromDB(): T?
}