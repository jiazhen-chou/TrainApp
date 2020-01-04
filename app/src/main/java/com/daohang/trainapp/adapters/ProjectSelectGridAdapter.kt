package com.daohang.trainapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.daohang.trainapp.R
import com.daohang.trainapp.components.ProjectItemView

class ProjectSelectGridAdapter(context: Context, nameList: List<String>) : BaseAdapter(){
    var mContext: Context = context
    var data: List<String> = nameList

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = ProjectItemView(mContext)
        val tvName: TextView = view.findViewById(R.id.tvProjectName)
        tvName.text = data[position]
        return view
    }

    override fun getItem(position: Int): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data.size
    }

}