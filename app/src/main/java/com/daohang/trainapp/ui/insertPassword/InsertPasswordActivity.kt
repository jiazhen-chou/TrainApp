package com.daohang.trainapp.ui.insertPassword

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import com.daohang.trainapp.R
import com.daohang.trainapp.ui.BaseActivity
import com.daohang.trainapp.ui.parameterSetting.ParameterSettingActivity
import com.daohang.trainapp.utils.TOTP
import kotlinx.android.synthetic.main.activity_insert_password.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

const val FLAG = "flag"

class InsertPasswordActivity : BaseActivity(){
    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setLayoutId(R.layout.activity_insert_password)
        setTitleVisible(View.INVISIBLE)

        super.onCreate(savedInstanceState)

        initView()
    }

    private fun initView(){

        ivPasswordVisible.onClick {
            passwordVisible = !passwordVisible
            ivPasswordVisible.imageResource = if (passwordVisible) R.drawable.pwd_visible else R.drawable.pwd_invisible
            etPassword.transformationMethod = if (!passwordVisible) PasswordTransformationMethod() else null
        }

        btnSubmit.onClick {
            if (etPassword.text != null && etPassword.text.toString().isNotBlank()){
                val pwd = etPassword.text.toString()
                if (pwd == TOTP.generateMyTOTP()){
                    toast("验证成功")
                    val flag = intent.getStringExtra(FLAG)
                    if (flag == "desktop")
                        loadSystemLauncher()
                    else
                        startActivity<ParameterSettingActivity>()
                    finish()
                } else{
                    toast("验证失败")
                }
            }
        }

        btnBack.onClick {
            finish()
        }
    }

    /**
     * 返回系统桌面
     */
    private fun loadSystemLauncher(){
        val manager = packageManager
        val intent = Intent()
        intent.setPackage("com.android.launcher3")
        val apps = manager.queryIntentActivities(intent, 0)
        if (apps.size > 0){
            val info = apps[0]
            intent.component = ComponentName(info.activityInfo.packageName, info.activityInfo.name)
            startActivity(intent)
        }else{
            toast("无法启动系统桌面")
        }
    }
}