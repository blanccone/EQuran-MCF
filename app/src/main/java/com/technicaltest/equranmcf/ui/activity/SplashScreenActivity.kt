package com.technicaltest.equranmcf.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import com.technicaltest.core.ui.activity.CoreActivity
import com.technicaltest.equranmcf.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : CoreActivity<ActivitySplashScreenBinding>() {

    override fun inflateLayout(inflater: LayoutInflater): ActivitySplashScreenBinding {
        return ActivitySplashScreenBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread {
            Thread.sleep(3000)
            runOnUiThread {
                DaftarSurahActivity.newInstance(this)
                finish()
            }
        }.start()
    }
}