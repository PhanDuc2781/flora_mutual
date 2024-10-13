package com.cmd.flora.view.authen.fragment

import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.cmd.flora.R
import com.cmd.flora.base.BaseVMFragment
import com.cmd.flora.base.popTo
import com.cmd.flora.base.pushTo
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.base.showCustomAlertDialog
import com.cmd.flora.databinding.FragmentForgotPasswordBinding
import com.cmd.flora.utils.bind
import com.cmd.flora.utils.gone
import com.cmd.flora.utils.textFlow
import com.cmd.flora.utils.visible
import com.cmd.flora.view.dialog.AlertData
import com.cmd.flora.view.authen.viewModel.ForgotPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment :
    BaseVMFragment<FragmentForgotPasswordBinding, ForgotPasswordViewModel>(
        FragmentForgotPasswordBinding::inflate
    ) {
    override val viewModel: ForgotPasswordViewModel by viewModels()

    companion object {
        const val EMAIL = "email"
    }

    override fun initView() {
        super.initView()
        binding.backResetPass1.setOnBackClickListener {
            popTo()
        }

        binding.btnSend.setOnSingleClickListener {
            viewModel.validateEmail(binding.edtEmail.text.toString())
        }

        binding.edtEmail.textFlow.bind(viewModel.observerEdittext, lifecycleScope) {
            viewModel.clearErrorTxt()
        }

        viewModel.observerEdittext.observe(viewLifecycleOwner) { text ->
            binding.btnSend.apply {
                isEnabled = text.isNotEmpty()
                background = AppCompatResources.getDrawable(
                    context,
                    if (isEnabled) R.drawable.bg_btn_login else R.drawable.bg_disable_btn_login
                )
            }
        }

        viewModel.errorFormatEmail.observe(viewLifecycleOwner) {
            binding.txtError.apply {
                text = it
                if (it.isNotEmpty()) visible() else gone()
            }
        }

        viewModel.errorMgs.observe(viewLifecycleOwner) {
            mActivity?.showCustomAlertDialog(
                AlertData(
                    title = it.title.toString(),
                    msg = it.errors?.email?.firstOrNull() ?: getString(R.string.msg_error_forgotpass_request),
                    posTitle = getString(R.string.ok)
                )
            )
        }

        viewModel.forgotPassRequestSuccess.observe(viewLifecycleOwner) {
            val email = binding.edtEmail.text.toString()
            it?.let {
                pushTo(
                    R.id.action_forgotPasswordFragment_to_forgotPassDoneFragment,
                    bundleOf(EMAIL to email)
                )
            }
        }
    }
}