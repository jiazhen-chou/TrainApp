package com.daohang.trainapp.services

import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle

class GPSService(val mContext: Context, val imei: String): LocationListener {

    private lateinit var locationManager: LocationManager

    fun startLocation(){
        locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        criteria.isSpeedRequired = true
        criteria.isBearingRequired = true

        criteria.horizontalAccuracy = Criteria.ACCURACY_HIGH
        criteria.verticalAccuracy = Criteria.ACCURACY_HIGH
        criteria.bearingAccuracy = Criteria.ACCURACY_LOW
        criteria.speedAccuracy = Criteria.ACCURACY_HIGH

        locationManager.requestLocationUpdates(locationManager.getBestProvider(criteria,true), 1000, 0F, this)
    }

    fun getGeoFencePoints(){

    }

    override fun onLocationChanged(location: Location?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderEnabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}