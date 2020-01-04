package com.daohang.trainapp.components

import android.app.Dialog
import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import com.daohang.trainapp.R
import com.daohang.trainapp.adapters.CarTypeRecyclerAdapter
import com.daohang.trainapp.utils.inRange
import kotlinx.android.synthetic.main.dialog_car_color.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.sdk27.coroutines.onClick

const val default_width = 300
const val default_height = 320
const val default_span_count = 2

class CarTypeDialog(context: Context) : Dialog(context) {

    private var title = "车牌颜色选择"
    private val colorList = mutableListOf<Int>()
    private val dataList = mutableListOf<String>()
    private var mSpanCount: Int = default_span_count
    private val defaultWidth: Int = 90
    private var defaultHeight: Int = 37

    private lateinit var adapter: CarTypeRecyclerAdapter
    private lateinit var listener: (Int, String) -> Unit


    private fun initView() {
        setContentView(R.layout.dialog_car_color)

        val layoutParams = window.attributes
        layoutParams.width = context.dip(defaultWidth * (mSpanCount + 3) + 6 * (mSpanCount - 1))
        layoutParams.height = context.dip(defaultHeight * (dataList.size / mSpanCount + 5) + 12 * (dataList.size / mSpanCount + 5))
        window.attributes = layoutParams

        tvTitle.text = "-$title-"
        recyclerView.layoutManager = GridLayoutManager(context, mSpanCount)
        adapter = CarTypeRecyclerAdapter(context, dataList, colorList)
        recyclerView.adapter = adapter

        btnSubmit.onClick {
            if (adapter.selectedItem.inRange(0..dataList.size)) {
                listener(adapter.selectedItem, dataList[adapter.selectedItem])
            }
            this@CarTypeDialog.cancel()
        }
    }

    fun measureSize(size: Pair<Int, Int>): Pair<Int, Int> {
        val width = size.first * (mSpanCount + 1) + 6 * (mSpanCount - 1)
        val height =
            size.first * (dataList.size / mSpanCount + 3) + 8 * (dataList.size / mSpanCount - 1)
        return Pair(width, height)
    }

    inner class Builder {

        fun setTitle(content: String): Builder {
            title = content
            return this
        }

        fun setColorList(list: MutableList<Int>): Builder {
            colorList.clear()
            colorList.addAll(list)
            return this
        }

        fun setDataList(list: MutableList<String>): Builder {
            dataList.clear()
            dataList.addAll(list)
            return this
        }

        fun setSpanCount(count: Int): Builder {
            mSpanCount = count
            return this
        }

        fun onSubmit(onClick: (Int, String) -> Unit): Builder {
            listener = onClick
            return this
        }

        fun build(): CarTypeDialog {
            initView()
            return this@CarTypeDialog
        }

    }
}