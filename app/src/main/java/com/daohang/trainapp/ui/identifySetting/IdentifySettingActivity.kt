package com.daohang.trainapp.ui.identifySetting

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.daohang.trainapp.R
import com.daohang.trainapp.services.globalPreferenceModel
import com.daohang.trainapp.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_identify_setting.*
import kotlinx.android.synthetic.main.component_header.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.sdk27.coroutines.onClick

class IdentifySettingActivity : BaseActivity(){

    val viewModel by lazy {
        ViewModelProviders.of(this)[IdentifySettingViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setLayoutId(R.layout.activity_identify_setting)

        super.onCreate(savedInstanceState)

        tvTitle.text = "身份验证方式"

        initView()
    }

    private fun initView(){

        btnBack.onClick { finish() }

        globalPreferenceModel.run {
            when(authType){
                1 -> {
                    ivCard.imageResource = R.mipmap.card_selected
                    ivBoth.imageResource = R.mipmap.both_normal
                    ivFace.imageResource = R.mipmap.face_normal
                }
                2 -> {
                    ivCard.imageResource = R.mipmap.card_normal
                    ivBoth.imageResource = R.mipmap.both_selected
                    ivFace.imageResource = R.mipmap.face_normal
                }
                3 -> {
                    ivCard.imageResource = R.mipmap.card_normal
                    ivBoth.imageResource = R.mipmap.both_normal
                    ivFace.imageResource = R.mipmap.face_selected
                }
            }
        }
    }
}