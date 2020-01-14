package com.daohang.trainapp.services

import androidx.lifecycle.MutableLiveData
import com.daohang.trainapp.utils.toHexString
import com.daohang.trainapp.utils.toInt2
import com.kongqw.serialportlibrary.SerialPortFinder
import com.kongqw.serialportlibrary.SerialPortManager
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener

var isObdOpen = MutableLiveData<Boolean>(false)

class ObdService(private val name: String, private val bandRate: Int) : OnSerialPortDataListener{

    companion object{
        var isObdEnable = false
    }

    private lateinit var serialPortManager: SerialPortManager
    val tempList = mutableListOf<Byte>()

    fun open() {
        serialPortManager = SerialPortManager()
        SerialPortFinder().devices.forEach {
            if (it.name == name)
                isObdOpen.postValue(serialPortManager.openSerialPort(it.file, bandRate))
        }
        serialPortManager.setOnSerialPortDataListener(this)
    }

    fun close() = serialPortManager.closeSerialPort()

    override fun onDataReceived(p0: ByteArray?) {
        p0?.let {
            tempList.addAll(it.toList())
            val size = tempList.size

            if (size > 2 && tempList[size - 1] == 0x0A.toByte() && tempList[size - 2] == 0x0D.toByte()){
                if (size > 5 && tempList[4] == 0x41.toByte() && tempList[5] == 0x02.toByte()){
                    val data = tempList.subList(6, size - 3).toByteArray()
                    val totalMiles = data.sliceArray(0 until 4).toHexString().toInt(16)
                    val totalFuel = data.sliceArray(4 until 8).toHexString().toInt(16)
                    val currentFuel = data.sliceArray(8 until 12).toHexString().toInt(16)
                    val voltage = data.slice(12 until 14).toInt2()
                    val speed = data[16].toInt()
                    val rpm = byteArrayOf(0x00, 0x00, data[14], data[15]).toHexString().toInt(16)

                    if (isObdEnable){
                        currentLocation?.run {
                            obd_miles = (totalMiles / 100).toFloat()
                            obd_fuel = (totalFuel / 100).toFloat()
                            record_speed = (speed * 10).toFloat()
                            rotate_speed = rpm
                        }
                    }

                    println("累计里程：$totalMiles, 累计油量：$totalFuel, 瞬时油耗：$currentFuel, 电压：$voltage, 速度：$speed, 转速：$rpm")

                    tempList.clear()
                }
            }
        }
    }

    override fun onDataSent(p0: ByteArray?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}