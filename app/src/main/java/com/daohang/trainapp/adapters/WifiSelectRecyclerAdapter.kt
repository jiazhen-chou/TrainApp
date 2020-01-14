package com.daohang.trainapp.adapters

import android.content.Context
import android.net.wifi.ScanResult
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daohang.trainapp.R
import com.daohang.trainapp.components.InputDialog
import com.daohang.trainapp.db.models.WifiScanResult
import com.daohang.trainapp.utils.color
import com.daohang.trainapp.utils.listen
import com.daohang.trainapp.utils.odd
import kotlinx.android.synthetic.main.item_wifi_list.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.collections.forEachByIndex
import org.jetbrains.anko.toast

class WifiSelectRecyclerAdapter(val context: Context, val dataList: MutableList<WifiScanResult>) : RecyclerView.Adapter<WifiSelectRecyclerAdapter.ViewHolder>() {

    var selectedItem: Int = 0
    var connectedIndex = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_wifi_list, parent, false))
        return viewHolder.listen { position, _ ->
            if (connectedIndex != position) {
                selectedItem = position
                InputDialog(context, dataList[position].ssid).show()
            } else {
                context.toast("您已连接到此wifi")
            }
        }
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == connectedIndex)
            holder.isConnected.visibility = View.VISIBLE
        else
            holder.isConnected.visibility = View.INVISIBLE

        if (position.odd()){
            holder.rootView.backgroundColor = context.color(R.color.selfcheck_white_bg)
        } else{
            holder.rootView.backgroundColor = context.color(android.R.color.transparent)
        }

        val result = dataList[position]
        val level =
            if (result.level < -80) R.mipmap.wifi_weak
            else if (result.level > -80 && result.level < -70) R.mipmap.wifi_normal
            else R.mipmap.wifi_strong
        holder.textView.text = "${result.ssid}"
        holder.imageView.setImageResource(level)
    }

    fun setDataList(currentSSID: String?, list: List<WifiScanResult>){
        dataList.clear()
        dataList.addAll(list)

        if (currentSSID.isNullOrEmpty())
            connectedIndex = -1
        else {
            dataList.forEachIndexed { index, wifiScanResult ->
                if (wifiScanResult.ssid == currentSSID) {
                    connectedIndex = index
                    return@forEachIndexed
                }
            }
        }

        if (connectedIndex != -1){
            val temp = dataList[connectedIndex]
            dataList[connectedIndex] = dataList[0]
            dataList[0] = temp
            connectedIndex = 0
        }

        notifyDataSetChanged()
    }


    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val rootView = view.viewRoot
        val textView = view.tvWifi
        val imageView = view.ivWifiSignal
        val isConnected = view.isConnected
    }
}