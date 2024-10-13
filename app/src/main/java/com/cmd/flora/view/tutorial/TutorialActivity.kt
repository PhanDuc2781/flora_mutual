package com.cmd.flora.view.tutorial

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import com.cmd.flora.application.storage
import com.cmd.flora.base.BaseActivity
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.databinding.ActivityTutorialBinding
import com.cmd.flora.utils.DeviceInfo
import com.cmd.flora.view.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TutorialActivity : BaseActivity<ActivityTutorialBinding>(ActivityTutorialBinding::inflate) {
    private val tutorialAdapter: TutorialAdapter by lazy { TutorialAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideNavigationBar()
    }

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        binding.viewPagerTutorial.updateLayoutParams<ConstraintLayout.LayoutParams> {
            this.matchConstraintPercentHeight = if (DeviceInfo.ratio > 0.55) 0.8F else 0.6995074F
        }
        tutorialAdapter.submitList(TutorialData.imageTutorialList())

        binding.viewPagerTutorial.adapter = tutorialAdapter
        binding.viewPagerTutorial.offscreenPageLimit = 5
        binding.dotsIndicator.attachTo(binding.viewPagerTutorial)

        binding.txtSkip.setOnSingleClickListener {
            storage.passTutorial = true
            startActivity(Intent(this@TutorialActivity, MainActivity::class.java))
            finish()
        }

        askNotificationPermission()
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                permissionsResult.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
            }
        }
    }

}

fun AppCompatActivity.hideNavigationBar() {
    WindowCompat.setDecorFitsSystemWindows(window, true)
    val controllerCompat = WindowInsetsControllerCompat(window, window.decorView)
    controllerCompat.hide(WindowInsetsCompat.Type.navigationBars())
    controllerCompat.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    controllerCompat.isAppearanceLightStatusBars = true
}