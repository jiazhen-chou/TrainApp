package com.daohang.trainapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import com.daohang.trainapp.MyApplication
import com.daohang.trainapp.constants.DEVICE_1700
import com.daohang.trainapp.constants.DEVICE_1800
import com.daohang.trainapp.constants.GPS_STATE_CHANGE
import com.daohang.trainapp.constants.SEND_TTS_MESSAGE
import com.daohang.trainapp.db.models.PhotoPrintInfo
import com.daohang.trainapp.db.models.SavedCardInfo
import com.daohang.trainapp.livebus.LocationStates
import com.daohang.trainapp.livebus.TtsMessage
import com.jeremyliao.liveeventbus.LiveEventBus
import com.yz.lz.modulapi.CardInfo
import com.yz.lz.modulapi.JNIUtils
import com.yz.lz.modulapi.NewJNIUtils
import kotlinx.coroutines.delay
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.Executors
import kotlin.concurrent.thread


val commonExecutor = Executors.newFixedThreadPool(5)

/**
 * 开始定位
 */
@SuppressLint("MissingPermission")
fun startLocation(context: Context) {
    MyApplication.locationStarted = true

    var lastLocation: Location? = null
    var canLocate = false

    val locationManager =
        context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, object :
        LocationListener {
        override fun onLocationChanged(location: Location?) {
            println("Location is ${location?.latitude},${location?.longitude}")
            lastLocation = location
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderEnabled(provider: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderDisabled(provider: String?) {
            println("AMap providerDisabled")
        }
    })
    locationManager?.addGpsStatusListener {
        val gpsStatus = locationManager.getGpsStatus(null)
        var count = 0
        gpsStatus?.satellites?.forEach {
            if (it.snr > 30)
                count++
        }
        canLocate = count >= 4
        MyApplication.locationEnabled = canLocate
    }

    thread {
        while (true) {
            LiveEventBus.get(GPS_STATE_CHANGE)
                .post(LocationStates(lastLocation, canLocate))
            Thread.sleep(5000)
        }
    }
}

/**
 * 检测rfid
 */
suspend fun checkRfid(): Boolean {
    var result: Boolean
    result = JNIUtils.getInstance().openRfidDevice()
    if (result) {
        MyApplication.DeviceType = DEVICE_1700
    } else {
        result = NewJNIUtils.getInstance().openRfidDevice()
        MyApplication.DeviceType = DEVICE_1800
    }

    return result
}

fun checkGps(): Boolean {
    //TODO  此处检测的是gps模块，若未开启，需要手动开启
    return MyApplication.checkGps
}

suspend fun checkObd(): Boolean {
    delay(200)
    return true
}

suspend fun checkNetwork(): Boolean {
    return MyApplication.checkNetwork
}

suspend fun checkCamera(): Boolean {
    delay(2000)
    return true
}

fun compressImage(file: File, pf: PhotoPrintInfo?, drawText: Boolean = true): ByteArray {
    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
    val mutableBitmap: Bitmap = bitmap.copy(bitmap.config, true)

    if (drawText)
        drawTextToBitmap(mutableBitmap, pf!!)

    val maxFileSize = 30
    val baos = ByteArrayOutputStream()
    var options = 100
    mutableBitmap.compress(
        Bitmap.CompressFormat.JPEG,
        options,
        baos
    ) //质量压缩方法，把压缩后的数据存放到baos中 (100表示不压缩，0表示压缩到最小)


    var baosLength = baos.toByteArray().size
    while (baosLength / 1024 > maxFileSize) { //循环判断如果压缩后图片是否大于maxMemmorrySize,大于继续压缩
        baos.reset() //重置baos即让下一次的写入覆盖之前的内容
        options = Math.max(0, options - 10) //图片质量每次减少10
        mutableBitmap.compress(Bitmap.CompressFormat.JPEG, options, baos) //将压缩后的图片保存到baos中
        baosLength = baos.toByteArray().size
        if (options == 0) //如果图片的质量已降到最低则，不再进行压缩
            break
    }

    return baos.toByteArray()
}

/**
 * 添加水印
 */
fun drawTextToBitmap(bitmap: Bitmap?, pf: PhotoPrintInfo) {
    bitmap?.let {
        val canvas = Canvas(it)
        // new antialised Paint
        val paint =
            Paint(Paint.FAKE_BOLD_TEXT_FLAG)
        // text color - #3D3D3D
        paint.color = Color.WHITE
        paint.textSize = 15.toFloat()
        paint.isDither = true //获取跟清晰的图像采样
        //        paint.setFilterBitmap(true);//过滤一些
        paint.strokeWidth = 3f //设置描边宽度
        paint.style = Paint.Style.FILL_AND_STROKE //对文字只描边
        val paint2 =
            Paint(Paint.FAKE_BOLD_TEXT_FLAG)
        //填充空心内容
        paint2.color = Color.RED
        paint2.textSize = 15.toFloat()
        paint.isDither = true //获取跟清晰的图像采样
        //        paint.setFilterBitmap(true);//过滤一些
        paint2.style = Paint.Style.FILL_AND_STROKE
        paint2.strokeWidth = 1f //设置描边宽度
        val bounds = Rect()
        var x: Int
        var y: Int
        pf.schoolName.let {
            x = 8
            y = 8
            paint.getTextBounds(it, 0, it.length, bounds)
            canvas.drawText(it, x.toFloat(), y + 16.toFloat(), paint)
            canvas.drawText(it, x.toFloat(), y + 16.toFloat(), paint2)
        }
        pf.carNum.let {
            x = 8
            y = 28
            paint.getTextBounds(it, 0, it.length, bounds)
            canvas.drawText(it, x.toFloat(), y + 16.toFloat(), paint)
            canvas.drawText(it, x.toFloat(), y + 16.toFloat(), paint2)
        }
        pf.deviceID.let {
            x = 100
            y = 28
            paint.getTextBounds(it, 0, it.length, bounds)
            canvas.drawText(it, x.toFloat(), y + 16.toFloat(), paint)
            canvas.drawText(it, x.toFloat(), y + 16.toFloat(), paint2)
        }
        pf.studentName.let {
            x = 8
            y = 115
            paint.getTextBounds(it, 0, it.length, bounds)
            canvas.drawText(it, x.toFloat(), y + 16.toFloat(), paint)
            canvas.drawText(it, x.toFloat(), y + 16.toFloat(), paint2)
        }
        pf.coachName.let {
            x = 8
            y = 135
            paint.getTextBounds(it, 0, it.length, bounds)
            canvas.drawText(it, x.toFloat(), y + 16.toFloat(), paint)
            canvas.drawText(it, x.toFloat(), y + 16.toFloat(), paint2)
        }
        pf.location.let {
            x = 8
            y = 155
            paint.getTextBounds(it, 0, it.length, bounds)
            canvas.drawText(it, x.toFloat(), y + 16.toFloat(), paint)
            canvas.drawText(it, x.toFloat(), y + 16.toFloat(), paint2)
        }
        pf.speed.let {
            x = 8
            y = 175
            paint.getTextBounds(it, 0, it.length, bounds)
            canvas.drawText(it, x.toFloat(), y + 16.toFloat(), paint)
            canvas.drawText(it, x.toFloat(), y + 16.toFloat(), paint2)
        }
        pf.datetime.let {
            x = 8
            y = 195
            paint.getTextBounds(it, 0, it.length, bounds)
            canvas.drawText(it, x.toFloat(), y + 16.toFloat(), paint)
            canvas.drawText(it, x.toFloat(), y + 16.toFloat(), paint2)
        }
        Log.d("TCP", "执行水印完毕")
        canvas.save()
        canvas.restore()
    }
}

/**
 * 语音播报
 */
fun sendTTSMsg(content: String) = SoundManager.startPlay(content)