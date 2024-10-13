package com.cmd.flora.view.fullscreenalert

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.cmd.flora.base.BaseActivity
import com.cmd.flora.base.MyActivity
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.databinding.ActivityFullscreenAlertBinding
import com.cmd.flora.utils.registerActivityResultLauncher

class FullScreenAlertActivity :
    BaseActivity<ActivityFullscreenAlertBinding>(ActivityFullscreenAlertBinding::inflate) {
    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        intent.extras?.getString(ARG_TITLE)?.let { binding.tvTitle.text = it }
        intent.extras?.getString(ARG_MESSAGE)?.let { binding.tvMessage.text = it }
        intent.extras?.getString(ARG_BTN_TITLE)?.let { binding.btnOK.text = it }

        onBackPressedDispatcher.addCallback {
            if (intent.extras?.getBoolean(ARG_CAN_GO_BACK, true) == true) {
                setResult(RESULT_OK)
                finish()
            }
        }

        binding.btnOK.setOnSingleClickListener {
            setResult(RESULT_OK)
            finish()
        }
    }

    data class Data(
        val title: String,
        val message: String,
        val btnActionTitle: String,
        val canGoBack: Boolean = true,
    )

    companion object {
        const val ARG_TITLE = "ARG_TITLE"
        const val ARG_MESSAGE = "ARG_MESSAGE"
        const val ARG_BTN_TITLE = "ARG_BTN_TITLE"
        const val ARG_CAN_GO_BACK = "ARG_CAN_GO_BACK"


        fun start(activity: MyActivity?, data: Data, completion: (() -> Unit)? = null) {
            var launcher: ActivityResultLauncher<Intent>? = null
            launcher = activity?.registerActivityResultLauncher(
                contract = ActivityResultContracts.StartActivityForResult(),
                callback = { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        completion?.invoke()
                        launcher?.unregister()
                    }
                }
            )

            launcher?.launch(
                Intent(
                    activity,
                    FullScreenAlertActivity::class.java
                ).apply {
                    putExtra(ARG_TITLE, data.title)
                    putExtra(ARG_MESSAGE, data.message)
                    putExtra(ARG_BTN_TITLE, data.btnActionTitle)
                    putExtra(ARG_CAN_GO_BACK, data.canGoBack)
                })
        }
    }
}