package com.daohang.trainapp.ui.carSetting

import android.os.Bundle
import android.view.View
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProviders
import com.daohang.trainapp.R
import com.daohang.trainapp.components.CarTypeDialog
import com.daohang.trainapp.components.SelectedInputView
import com.daohang.trainapp.constants.SP_VALIDATION
import com.daohang.trainapp.db.models.PreferenceModel
import com.daohang.trainapp.db.models.VehiclePreferenceModel
import com.daohang.trainapp.ui.BaseActivity
import com.daohang.trainapp.ui.WelcomeActivity
import com.daohang.trainapp.utils.getValidateSp
import com.daohang.trainapp.utils.inRange
import kotlinx.android.synthetic.main.activity_car_setting.*
import kotlinx.android.synthetic.main.component_header.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class CarSettingActivity : BaseActivity(), View.OnClickListener {

    private val CAR_TYPE = mutableListOf("A1", "A2", "A3", "B1", "B2", "C1", "C2", "C3", "C4")

    private var vehiclePreferenceModel = VehiclePreferenceModel()

    //项目id
    var project: PreferenceModel? = null

    lateinit var viewModel: CarSettingViewModel

    //选中的省份index
    var provinceIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        setLayoutId(R.layout.activity_car_setting)

        super.onCreate(savedInstanceState)

        tvTitle.text = getString(R.string.title_car_setting)

        intent.extras?.let {
            project = it["project"] as PreferenceModel?
        }

        viewModel = ViewModelProviders.of(this)[CarSettingViewModel::class.java]

        inputCarType.setClickListener(this)
        inputCarNumber.setClickListener(this)
        inputCarColor.setClickListener(this)
        inputProvince.setClickListener(this)
        inputCity.setClickListener(this)

        btnSubmit.onClick {
            if (inputId.content == null)
                toast("请输入设备id")
            else if (inputCarNumber.content == null)
                toast("请输入车牌号")
            else {
                vehiclePreferenceModel.clientId = inputId.content
                vehiclePreferenceModel.vehicleNumber = inputCarNumber.content
                project?.vehiclePreference = vehiclePreferenceModel

                project?.let {
                    it.vehiclePreference = vehiclePreferenceModel
                    viewModel.savePreference(it)
                }


                startActivity<WelcomeActivity>()
            }
        }
    }

    override fun onClick(v: View?) {
        val view = v as SelectedInputView?
        view?.let {
            when (it) {
                inputCarType -> {
                    CarTypeDialog(this).Builder()
                        .setSpanCount(4)
                        .setDataList(CAR_TYPE)
                        .onSubmit { _, str ->
                            inputCarType.content = str
                            vehiclePreferenceModel.vehicleType = str
                        }
                        .build()
                        .show()
                }
                inputCarColor -> {
                    CarTypeDialog(this).Builder()
                        .setTitle("车牌颜色选择")
                        .setColorList(
                            mutableListOf(
                                R.color.car_color_yellow,
                                R.color.car_color_blue,
                                R.color.car_color_white,
                                R.color.car_color_greeen,
                                R.color.car_color_black
                            )
                        )
                        .setDataList(mutableListOf("黄色", "蓝色", "白色", "绿色", "黑色"))
                        .onSubmit { _, str ->
                            inputCarColor.content = str
                            vehiclePreferenceModel.vehicleColor = str
                        }
                        .build()
                        .show()
                }
                inputProvince -> {
                    CarTypeDialog(this).Builder()
                        .setSpanCount(8)
                        .setTitle("请选择省")
                        .setDataList(viewModel.getProvinceStringList())
                        .onSubmit { index, str ->
                            provinceIndex = index
                            inputProvince.content = str
                            vehiclePreferenceModel.province = str
                        }
                        .build()
                        .show()
                }
                inputCity -> {
                    if (provinceIndex.inRange(0..viewModel.getProvinceStringList().size)) {
                        CarTypeDialog(this).Builder()
                            .setSpanCount(8)
                            .setTitle("请选择市")
                            .setDataList(viewModel.getCityStringList(provinceIndex))
                            .onSubmit { index, str ->
                                inputCity.content = str
                                vehiclePreferenceModel.city = str
                            }
                            .build()
                            .show()
                    } else {
                        toast("请先选择省")
                    }
                }
                else -> Unit
            }
        }
    }

}