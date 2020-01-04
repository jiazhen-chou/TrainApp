package com.daohang.trainapp.ui.parameterSetting

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.daohang.trainapp.R
import com.daohang.trainapp.ui.BaseActivity
import com.daohang.trainapp.ui.carInfo.CarInfoActivity
import com.daohang.trainapp.ui.identifySetting.IdentifySettingActivity
import com.daohang.trainapp.ui.selfcheck.SelfCheckActivity
import com.daohang.trainapp.ui.systemSetting.SystemSettingActivity
import com.daohang.trainapp.ui.trainParameter.TrainParameterActivity
import kotlinx.android.synthetic.main.activity_parameter_setting.*
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class ParameterSettingActivity : BaseActivity(){

    val viewModel by lazy {
        ViewModelProviders.of(this)[ParameterSettingViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setLayoutId(R.layout.activity_parameter_setting)

        super.onCreate(savedInstanceState)

        initView()
    }

    private fun initView(){

        lnCarInfo.onClick {
            startActivity<CarInfoActivity>()
        }

        lnIdentify.onClick {
            startActivity<IdentifySettingActivity>()
        }

        lnTrainParam.onClick {
            startActivity<TrainParameterActivity>()
        }

        lnSystemParam.onClick {
            startActivity<SystemSettingActivity>()
        }

        btnBack.onClick {
            finish()
        }

        btnFactorySet.onClick {
            viewModel.factoryReset()

            val intent = intentFor<SelfCheckActivity>()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}