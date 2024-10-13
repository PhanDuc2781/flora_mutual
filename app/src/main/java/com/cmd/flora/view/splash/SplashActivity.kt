package com.cmd.flora.view.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.cmd.flora.R
import com.cmd.flora.application.storage
import com.cmd.flora.base.launch
import com.cmd.flora.utils.getInset
import com.cmd.flora.view.main.MainActivity
import com.cmd.flora.view.tutorial.TutorialActivity
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        lifecycleScope.launch { getInset() }
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback { }
        splashScreen.setKeepOnScreenCondition { true }
        launch {
            val delayTime = 1500L
            val delaySuccess = async { delayComplete(delayTime) }
            val token = async { refreshToken() }

            if (delaySuccess.await() && token.await()) {
                if (storage.passTutorial) {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                } else {
                    startActivity(Intent(this@SplashActivity, TutorialActivity::class.java))
                }
                finish()
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        }
    }

    private suspend fun delayComplete(time: Long): Boolean {
        delay(time)
        return true
    }

    private suspend fun refreshToken(): Boolean = true
}