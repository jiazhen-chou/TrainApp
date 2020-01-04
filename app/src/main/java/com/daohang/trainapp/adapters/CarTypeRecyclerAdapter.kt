package com.daohang.trainapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.daohang.trainapp.R
import com.daohang.trainapp.utils.color
import com.daohang.trainapp.utils.drawable
import com.daohang.trainapp.utils.listen
import kotlinx.android.synthetic.main.item_car_type.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColor

class CarTypeRecyclerAdapter(val context: Context, val dataList: MutableList<String>, val colorList: MutableList<Int>) : RecyclerView.Adapter<CarTypeRecyclerAdapter.ViewHolder>(){

    var selectedItem = -1
    var size: MutableLiveData<Pair<Int, Int>> = MutableLiveData()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_car_type, parent, false))

        return holder.listen { position, type ->
            selectedItem = position
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = dataList[position]
        if (colorList.size > position){
            holder.tvName.textColor = context.color(colorList[position])
        }
        if (selectedItem == position){
            holder.lnRoot.backgroundDrawable = context.drawable(R.drawable.btn_border_blue)
        } else{
            holder.lnRoot.backgroundDrawable = context.drawable(R.drawable.btn_with_border)
        }
    }

//    fun getSize() = size.value

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvName = view.tvCarType
        val lnRoot = view.lnRoot
    }
}