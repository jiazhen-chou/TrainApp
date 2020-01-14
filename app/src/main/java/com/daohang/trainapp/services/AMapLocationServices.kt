package com.daohang.trainapp.services

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.amap.api.fence.GeoFence
import com.amap.api.fence.GeoFenceClient
import com.amap.api.fence.GeoFenceListener
import com.amap.api.location.*
import com.daohang.trainapp.db.models.LocationModel
import com.daohang.trainapp.db.models.Point
import com.daohang.trainapp.utils.GFG
import com.daohang.trainapp.utils.GpsUtil
import com.daohang.trainapp.utils.WarningFlag
import com.daohang.trainapp.utils.sendTTSMsg
import java.util.*
import kotlin.collections.HashMap

const val GEOFENCE_BROADCAST_ACTION = "com.location.apis.geofencedemo.broadcast"

var lastLongitude = MutableLiveData<Double>()
var lastLatitude = MutableLiveData<Double>()
var currentDistance = MutableLiveData<Double>()
var currentSpeed = MutableLiveData<Double>()

var outOfGeoFence: Boolean = false

val polygons: MutableMap<Int, List<Point>> = HashMap()

class AMapLocationServices(val context: Application) : AMapLocationListener{

    private val TAG = "AMapLocationService"
    private var lastLocation: LocationModel? = null

    init {
        val client = AMapLocationClient(context)
        client.setLocationListener(this)
        client.setLocationOption(AMapLocationClientOption().apply {
            locationPurpose = AMapLocationClientOption.AMapLocationPurpose.Sport
        })
        client.stopLocation()
        client.startLocation()

        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (isTraining && outOfGeoFence)
                    sendTTSMsg("超出教学区域")
            }
        },0,10 * 1000)
    }

    override fun onLocationChanged(location: AMapLocation?) {
        location?.run {
            if (errorCode == 0) {
                val gps = GpsUtil.toWGS84Point(latitude, longitude)
                val gpsLatitude = gps[0]
                val gpsLongitude = gps[1]
                lastLocation = currentLocation

                //更新当前坐标
                currentLocation = LocationModel(
                    0, 0, gpsLatitude, gpsLongitude, speed, speed, bearing.toInt()
                )

                //计算距离
                lastLocation?.let { last ->
                    currentLocation?.let { current ->
                        var distance = GpsUtil.distance(
                            last.latitude,
                            last.longitude,
                            current.latitude,
                            current.longitude
                        )

                        //按照时速最大120km/h来计算，若速度超过,则忽略此次数据
                        if (distance > 37.5)
                            distance = 0.0

                        currentDistance.postValue(distance)
                        currentSpeed.postValue(distance * 1.8)

                        println("距离为：$distance")
                    }
                }

                lastLongitude.postValue(gpsLongitude)
                lastLatitude.postValue(gpsLatitude)

                if (isTraining) {
                    polygons[currentClassItem.toInt()]?.let {
                        if (GFG.isInside(
                                it.toTypedArray(),
                                it.size,
                                Point((longitude * 1000000).toInt(), (latitude * 1000000).toInt())
                            )
                        ) {
                            outOfGeoFence = true
                            WarningFlag.setOutOfRangeWarning(currentClassItem, '1')
                        }
                        else {
                            outOfGeoFence = false
                            WarningFlag.setOutOfRangeWarning(currentClassItem, '0')
                        }
                    }
                }


                Log.d(
                    TAG,
                    "longitude: ${longitude}, latitude: ${latitude}, speed: ${speed}, direction: ${bearing}"
                )
            } else {
                Log.e(TAG, "errCode: $errorCode, errMsg: $errorInfo")
            }
        }
    }

}

/**
 * 解析围栏数据
 */
fun separatePolygonString(polygon: String): List<Point> {
    val result = mutableListOf<Point>()

    polygon.split(";").forEach {
        it.split(",").run {
            val point = GpsUtil.toGCJ02Point(get(1).toDouble(), get(0).toDouble())
            println("大地转高德坐标系，经度：${point[1]}, 纬度: ${point[0]}")
            result.add(Point((point[1] * 1000000).toInt(), (point[0] * 1000000).toInt()))
        }
    }
    return result
}
