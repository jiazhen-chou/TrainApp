package com.daohang.trainapp.ui.systemSetting

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.daohang.trainapp.R
import com.daohang.trainapp.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_system_setting.*
import kotlinx.android.synthetic.main.component_header.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class SystemSettingActivity : BaseActivity(){

    val viewModel by lazy {
        ViewModelProviders.of(this)[SystemSettingViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setLayoutId(R.layout.activity_system_setting)

        super.onCreate(savedInstanceState)

        tvTitle.text = "系统参数"

        initView()

        btnBack.onClick { finish() }
    }

    private fun initView() {
        viewModel.getPreferenceModel().observe(this, Observer {
            if (it.isNotEmpty()) {
                etDomain.setText(it[0].webSocketDomain)
                etPort.setText("${it[0].webSocketPort}")
            }

        })
    }
}