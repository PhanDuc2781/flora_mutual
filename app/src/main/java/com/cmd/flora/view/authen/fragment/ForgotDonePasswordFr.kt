package com.cmd.flora.view.authen.fragment

import androidx.navigation.fragment.findNavController
import com.cmd.flora.R
import com.cmd.flora.base.BaseFragment
import com.cmd.flora.base.pushTo
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.databinding.FragmentForgotPasswordDoneBinding
import com.cmd.flora.view.authen.fragment.ForgotPasswordFragment.Companion.EMAIL
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotDonePasswordFr :
    BaseFragment<FragmentForgotPasswordDoneBinding>(
        FragmentForgotPasswordDoneBinding::inflate
    ) {

    override fun initView() {
        super.initView()

        binding.edtEmail.setText(arguments?.getString(EMAIL))

        binding.imgBackResetPass2.setOnBackClickListener {
            findNavController().popBackStack()
        }

        binding.btnReturnLogin.setOnSingleClickListener {
            pushTo(R.id.actionToLoginFragement)
        }
    }
}