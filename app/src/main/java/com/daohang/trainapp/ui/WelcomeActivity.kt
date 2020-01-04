package com.daohang.trainapp.ui

import android.os.Bundle
import android.view.View
import com.daohang.trainapp.R
import com.daohang.trainapp.ui.main.MainActivity
import org.jetbrains.anko.startActivity
import java.util.*

class WelcomeActivity : BaseActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        setLayoutId(R.layout.activity_welcome)
        setTitleVisible(View.INVISIBLE)
        super.onCreate(savedInstanceState)


        Timer().schedule(object : TimerTask() {
            override fun run() {
                startActivity<MainActivity>()
            }
        },3000)
    }

}