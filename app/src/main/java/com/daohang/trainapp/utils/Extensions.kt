package com.daohang.trainapp.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Environment
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.daohang.trainapp.BuildConfig
import com.daohang.trainapp.constants.SP_RECORD_ID
import com.daohang.trainapp.constants.SP_VALIDATION
import com.google.gson.Gson
import java.nio.charset.Charset

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
    itemView.setOnClickListener {
        event.invoke(adapterPosition, itemViewType)
    }
    return this
}

fun Context.updatePath() = Environment.getExternalStorageDirectory().toString() + "/Update"

fun Context.defaultFilePath() = filesDir

fun Context.color(resId: Int) = ContextCompat.getColor(this, resId)

fun Context.drawable(resId: Int) = ContextCompat.getDrawable(this, resId)

fun Context.hasPermission(permission: String) =
    this.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

fun Context.setWifi() = startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))

fun Context.getValidateSp() = getSharedPreferences(SP_VALIDATION, Context.MODE_PRIVATE)

fun Context.getRecordIdSp() = getSharedPreferences(SP_RECORD_ID, Context.MODE_PRIVATE)

fun versionName(): String = BuildConfig.VERSION_NAME

fun versionCode(): Int = BuildConfig.VERSION_CODE

inline fun <reified T> Context.readJsonFromFile(fileName: String) =
    Gson().fromJson<T>(assets.open(fileName).bufferedReader().use { it.readText() }, T::class.java)

fun screenWidth() = Resources.getSystem().displayMetrics.widthPixels

fun screenHeight() = Resources.getSystem().displayMetrics.heightPixels

/**
 * 数字是否在[start,end)范围内
 */
fun Int.inRange(range: IntRange): Boolean = this >= range.first && this < range.last

fun Int.odd(): Boolean = this % 2 != 0

fun Int.between(min: Int, max: Int) = this in (min + 1) until max

fun Int.moreThan(value: Int) = this > value

fun Int.lessThan(value: Int) = this < value

fun Int.toByteArray2() = byteArrayOf(
    (this shr 8).toByte(),
    this.toByte()
)

fun Int.toByteArray4() = byteArrayOf(
    (this shr 24).toByte(),
    (this shr 16).toByte(),
    (this shr 8).toByte(),
    this.toByte()
)

fun Short.toByteArray() = byteArrayOf(
    (this.toInt() shr 8).toByte(),
    this.toByte()
)

/**
 * byte转二进制字符串
 */
fun Byte.toBin(): String {
    val temp = toString(2)
    val length = 8 - temp.length
    val buffer = StringBuffer()
    for (index in 0 until length)
        buffer.append('0')
    buffer.append(temp)
    return buffer.toString()
}

fun Long.toByteArray() = byteArrayOf(
    (this shr 24).toByte(),
    (this shr 16).toByte(),
    (this shr 8).toByte(),
    (this).toByte()
)

fun String.toHexInt() = (get(0).toInt() and 0xFF) * 256 + (get(1).toInt() and 0xFF)

fun String.toAscii(): ByteArray{
    var bytes = ByteArray(length)
    for ((index, value) in withIndex()){
        bytes[index] = value.toByte()
    }
    return bytes
}

fun ByteArray.ascii2String(): String{
    val buffer = StringBuffer()
    forEach {
        buffer.append(it.toChar())
    }
    return buffer.toString()
}

fun ByteArray.toHexInt() = (get(0).toInt() and 0xFF) * 256 + (get(1).toInt() and 0xFF)

fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

fun ByteArray.hexStringToInt() = this.toHexString().toInt()

fun List<Byte>.toString(charset: String) =
    this.toByteArray().toString(charset = Charset.forName(charset))

fun List<Byte>.toInt(): Int =
    ((this[3].toInt() and 0xFF) shl 24) or ((this[2].toInt() and 0xFF) shl 16) or ((this[1].toInt() and 0xFF) shl 8) or (this[0].toInt() and 0xFF)

fun List<Byte>.toShort(): Short = ((this[1].toInt() shl 8) or (this[0].toInt() and 0xFF)).toShort()

fun List<Byte>.toInt2(): Int = ((this[0].toInt() and 0xFF) shr 8) or (this[1].toInt() and 0xFF)

fun List<Byte>.bcd2Str(): String {
    val temp = StringBuffer(this.size * 2)
    this.forEach {
        temp.append((it.toInt() and 0xF0) ushr 4)
        temp.append(it.toInt() and 0x0F)
    }
    return if (temp.toString().substring(
            0,
            1
        ) == "0"
    ) temp.toString().substring(1) else temp.toString()
}