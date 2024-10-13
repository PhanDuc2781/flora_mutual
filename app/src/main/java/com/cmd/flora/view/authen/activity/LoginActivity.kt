package com.cmd.flora.view.authen.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.os.bundleOf
import com.cmd.flora.R
import com.cmd.flora.base.BaseActivity
import com.cmd.flora.base.noInset
import com.cmd.flora.base.pushTo
import com.cmd.flora.data.model.WindowInsetModel
import com.cmd.flora.databinding.ActivityLoginBinding
import com.cmd.flora.utils.orNull
import com.cmd.flora.view.authen.activity.LoginActivity.Companion.EMAIL
import dagger.hilt.android.AndroidEntryPoint

fun Intent.getCode(): String? = data?.pathSegments?.lastOrNull()
fun Intent.getEmail(): String? = data?.getQueryParameter(EMAIL)

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {
    companion object {
        const val CODE = "code"
        const val EMAIL = "email"
    }

    override fun setupView(savedInstanceState: Bundle?) {
        super.setupView(savedInstanceState)
        (intent.getCode() to intent.getEmail()).orNull()?.let {
            pushTo(R.id.actionToResetPassFragment, bundleOf(CODE to it.first, EMAIL to it.second))
        }
    }

    override fun setupInset(insets: WindowInsetModel) {
        super.setupInset(insets)
        noInset()
        binding.containerLogin.setPadding(0, insets.top, 0, insets.bottom)
    }

    override var navHostId: Int? = R.id.container_login
    override var rootDes: Int? = R.id.loginFragment

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}