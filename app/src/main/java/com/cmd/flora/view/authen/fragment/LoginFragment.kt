package com.cmd.flora.view.authen.fragment

import android.app.Activity
import android.content.Intent
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.cmd.flora.BuildConfig
import com.cmd.flora.R
import com.cmd.flora.base.BaseVMFragment
import com.cmd.flora.base.pushTo
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.base.showCustomAlertDialog
import com.cmd.flora.databinding.FragmentLoginBinding
import com.cmd.flora.utils.bind
import com.cmd.flora.utils.gone
import com.cmd.flora.utils.textFlow
import com.cmd.flora.utils.visible
import com.cmd.flora.view.dialog.AlertData
import com.cmd.flora.view.authen.activity.LoginActivity
import com.cmd.flora.view.authen.viewModel.LoginViewModel
import com.cmd.flora.view.main.MainActivity.Companion.StartActivityForResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine

@AndroidEntryPoint
class LoginFragment :
    BaseVMFragment<FragmentLoginBinding, LoginViewModel>(FragmentLoginBinding::inflate) {
    override val viewModel: LoginViewModel by viewModels()

    override fun initView() {
        super.initView()

        binding.txtForgotPassword.setOnSingleClickListener {
            pushTo(R.id.action_loginFragment_to_forgotPasswordFragment)
        }

        binding.backLogin.setOnBackClickListener {
            activity?.finish()
        }

        binding.btnLogin.setOnSingleClickListener {
            viewModel.validateAccount(
                binding.edtId.text.toString(),
                binding.edtPassword.text.toString()
            )
        }

        binding.edtId.textFlow.combine(binding.edtPassword.textFlow) { txt1, txt2 ->
            Pair(txt1, txt2)
        }.bind(viewModel.mergeEdittext, lifecycleScope) {
            viewModel.clearErrorText()
        }

        viewModel.mergeEdittext.observe(viewLifecycleOwner) { (text1, text2) ->
            binding.btnLogin.apply {
                isEnabled = text1.isNotEmpty() && text2.isNotEmpty()
                background = AppCompatResources.getDrawable(
                    context,
                    if (isEnabled) R.drawable.bg_btn_login else R.drawable.bg_disable_btn_login
                )
            }
        }

        viewModel.validateAccountId.observe(viewLifecycleOwner) { message ->
            binding.txtErrorAccountID.apply {
                text = message
                if (message.isNotEmpty()) visible() else gone()
            }
        }

        viewModel.validatePassword.observe(viewLifecycleOwner) { message ->
            binding.txtErrorPassword.apply {
                text = message
                if (message.isNotEmpty()) visible() else gone()
            }
        }


        viewModel.loginStateSuccess.observe(viewLifecycleOwner) { state ->
            //return home
            if (state) {
                activity?.apply {
                    setResult(Activity.RESULT_OK, Intent().apply {
                        putExtra(StartActivityForResult, LoginActivity::class.java.name)
                    })
                    this.finish()
                }
            }
        }

        viewModel.errorMessageRequest.observe(viewLifecycleOwner) {
<<<<<<< HEAD
=======
            if (it.isEmpty()) return@observe
>>>>>>> 19f69cb82a1f2f5a2f30fa6c5f44172ba5fad5cc
            mActivity?.showCustomAlertDialog(AlertData(msg = it, posTitle = getString(R.string.ok)))
        }

        //for testing
        if (BuildConfig.DEBUG) {
            BuildConfig.testMemberCode.ifNotEmpty { binding.edtId.setText(it) }
            BuildConfig.testPassword.ifNotEmpty { binding.edtPassword.setText(it) }
        }
    }
}

fun String.ifNotEmpty(handle: (String) -> Unit) {
    if (this.removeNull().isNotEmpty()) handle(this)
}

fun String.removeNull(): String = if (this.lowercase() == "null") "" else this
