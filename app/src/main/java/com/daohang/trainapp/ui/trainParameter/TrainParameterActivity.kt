package com.daohang.trainapp.ui.trainParameter

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.daohang.trainapp.R
import com.daohang.trainapp.db.models.TrainPreferenceModel
import com.daohang.trainapp.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_train_parameter.*
import kotlinx.android.synthetic.main.component_header.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast

class TrainParameterActivity : BaseActivity(){

    //TODO 车型验证输入栏显示验证规则需要实现

    private lateinit var viewModel: TrainParameterViewModel;

    override fun onCreate(savedInstanceState: Bundle?) {
        setLayoutId(R.layout.activity_train_parameter)

        super.onCreate(savedInstanceState)

        tvTitle.text = "培训参数设置"

        viewModel = ViewModelProviders.of(this)[TrainParameterViewModel::class.java]

        viewModel.fetchProjectTrainPreference()

        viewModel.trainPreferenceLiveData.observe(this, Observer {
            initData(it)
        })

        btnBack.onClick { finish() }

//        paCoachCar.onClick {
//            toast("验证车型")
//        }
    }

    private fun initData(model: TrainPreferenceModel?){
        model?.let {
            if (it.picture_interval != -1){
                paPicture.setContent("${it.picture_interval}分钟")
                paPicture.setLocked(false)
            } else{
                paPicture.setLocked(true)
            }

            if (it.max_speed != -1){
                paOverSpeed.setContent("${it.max_speed}KM/H")
                paOverSpeed.setLocked(false)
            } else {
                paOverSpeed.setLocked(true)
            }

            if (it.max_hour != -1){
                paMaxHour.setContent("${it.max_hour}分钟")
                paMaxHour.setLocked(false)
            } else {
                paMaxHour.setLocked(true)
            }

            paGeoFence.setLocked(it.use_fence == -1)

            paOpenObd.setContent(if (it.use_obd == -1) "模拟" else "真实")
            paOpenObd.setLocked(it.use_obd == -1)

            paCoachCar.setLocked(it.check_car_type == "-1")
        }
    }
}