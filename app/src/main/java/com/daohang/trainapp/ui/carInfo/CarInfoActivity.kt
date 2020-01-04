package com.daohang.trainapp.ui.carInfo

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.daohang.trainapp.R
import com.daohang.trainapp.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_car_info.*
import kotlinx.android.synthetic.main.component_header.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class CarInfoActivity : BaseActivity(){

    val viewModel by lazy {
        ViewModelProviders.of(this)[CarInfoViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setLayoutId(R.layout.activity_car_info)

        super.onCreate(savedInstanceState)

        tvTitle.text = "车辆信息"

        initView()
    }

    private fun initView() {
        viewModel.getCarInfo().observe(this, Observer {
            if (it.isNotEmpty()){
                it[0].vehiclePreference.run {
                    inputId.content = clientId
                    inputCarType.content = vehicleType
                    inputCarNumber.content = vehicleNumber
                    inputCarColor.content = vehicleColor
                    inputProvince.content = province
                    inputCity.content = city
                }
            }
        })

        btnSubmit.onClick { finish() }
    }

}