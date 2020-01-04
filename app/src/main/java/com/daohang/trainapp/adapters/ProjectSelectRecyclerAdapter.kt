package com.daohang.trainapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daohang.trainapp.R
import com.daohang.trainapp.db.models.PreferenceModel
import com.daohang.trainapp.utils.listen
import kotlinx.android.synthetic.main.item_project.view.*
import org.jetbrains.anko.backgroundColor

class ProjectSelectRecyclerAdapter(val items: List<PreferenceModel>, val context: Context): RecyclerView.Adapter<ProjectSelectRecyclerAdapter.ViewHolder>(){
    var selectedItem = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder = ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_project,parent,false))

        return holder.listen { position, type ->
            selectedItem = position
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = items[position].projectName
        holder.viewRoot.backgroundColor = if (selectedItem == position) context.resources.getColor(R.color.button_blue) else context.resources.getColor(R.color.bg_black)
    }

    fun getSelectedItemPosition() = selectedItem


    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val tvName = view.tvProjectName
        val viewRoot = view.viewRoot
    }

}