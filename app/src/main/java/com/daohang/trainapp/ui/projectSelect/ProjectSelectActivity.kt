package com.daohang.trainapp.ui.projectSelect

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.daohang.trainapp.R
import com.daohang.trainapp.adapters.ProjectSelectRecyclerAdapter
import com.daohang.trainapp.db.models.PreferenceModel
import com.daohang.trainapp.ui.BaseActivity
import com.daohang.trainapp.ui.carSetting.CarSettingActivity
import kotlinx.android.synthetic.main.activity_project_select.*
import kotlinx.android.synthetic.main.component_header.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class ProjectSelectActivity : BaseActivity(){

    private lateinit var viewModel: ProjectSelectViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setLayoutId(R.layout.activity_project_select)

        super.onCreate(savedInstanceState)

        tvTitle.text = getString(R.string.title_project_select)

        viewModel = ViewModelProviders.of(this)[ProjectSelectViewModel::class.java]

        viewModel.fetchProjects()

        viewModel.projectsLiveData.observe(this, Observer<MutableList<PreferenceModel>> {
            if (it.size == 0){
                prepareFailedView()
            }else{
                prepareRecyclerView(it.toList())
            }
        })
    }

    /**
     * 数据获取成功后
     */
    private fun prepareRecyclerView(dataList: List<PreferenceModel>){
        lnSuccess.visibility = View.VISIBLE
        lnFailed.visibility = View.INVISIBLE

        val adapter = ProjectSelectRecyclerAdapter(dataList,this)
        val gridLayoutManager = GridLayoutManager(this, 2)
        gvProjects.layoutManager = gridLayoutManager
        gvProjects.adapter = adapter

        btnSubmit.onClick {

            if (adapter.selectedItem == -1)
                toast("请选择项目")
            else {
                val project = dataList[adapter.selectedItem]
                startActivity<CarSettingActivity>("project" to project)
            }
        }
    }

    private fun prepareFailedView(){
        lnSuccess.visibility = View.INVISIBLE
        lnFailed.visibility = View.VISIBLE

        btnRetry.onClick {
            viewModel.fetchProjects()
            lnSuccess.visibility = View.INVISIBLE
            lnFailed.visibility = View.INVISIBLE
        }
    }
}