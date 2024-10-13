package com.cmd.flora.view.authen.fragment.resetpass

import androidx.activity.addCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import com.cmd.flora.R
import com.cmd.flora.base.BaseVMFragment
import com.cmd.flora.base.pushTo
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.base.showCustomAlertDialog
import com.cmd.flora.databinding.FragmentResetPassBinding
import com.cmd.flora.view.dialog.AlertData
import com.cmd.flora.view.authen.activity.LoginActivity.Companion.CODE
import com.cmd.flora.view.authen.activity.LoginActivity.Companion.EMAIL
import com.cmd.flora.view.authen.viewModel.ResetPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPassFragment :
    BaseVMFragment<FragmentResetPassBinding, ResetPasswordViewModel>(FragmentResetPassBinding::inflate) {

    override val viewModel: ResetPasswordViewModel by viewModels()

    override fun initView() {
        super.initView()
        mActivity?.let {
            activity?.onBackPressedDispatcher?.addCallback(it) {
                activity?.finish()
            }
        }
        val (code, email) = arguments?.let { it.getString(CODE) to it.getString(EMAIL) }
            ?: (null to null)

        viewModel.mergerEdittext(binding.edtNewPass, binding.edtConfirmPass)

        viewModel.mergerEdittext.observe(viewLifecycleOwner) { (text1, text2) ->
            binding.btnSetNewPass.apply {
                isEnabled = text1.isNotEmpty() && text2.isNotEmpty()
                background = AppCompatResources.getDrawable(
                    context,
                    if (isEnabled) R.drawable.bg_btn_login else R.drawable.bg_disable_btn_login
                )
            }
        }

        binding.btnSetNewPass.setOnSingleClickListener {
            val newPass = binding.edtNewPass.text.toString().trim()
            val confirmNewPass = binding.edtConfirmPass.text.toString().trim()

            code?.let { it1 ->
                email?.let { it2 ->
                    viewModel.validateNewPass(newPass, confirmNewPass, it1, it2)
                }
            }
        }

        viewModel.errorMessageRequest.observe(viewLifecycleOwner) {
            it.let {
                mActivity?.showCustomAlertDialog(
                    AlertData(
                        msg = it.detail?.message.toString(),
                        posTitle = getString(R.string.ok)
                    )
                )
            }
        }

        viewModel.errorMessageValid.observe(viewLifecycleOwner) {
            mActivity?.showCustomAlertDialog(
                AlertData(
                    msg = it,
                    posTitle = getString(R.string.ok)
                )
            )
        }

        viewModel.resetPasswordResponse.observe(viewLifecycleOwner) {
            it?.let {
                pushTo(R.id.action_resetPassFragment_to_resetPassDoneFragment)
            }
        }
    }
}