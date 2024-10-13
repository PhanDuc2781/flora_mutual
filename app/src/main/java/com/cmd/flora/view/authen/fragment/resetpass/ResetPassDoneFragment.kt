package com.cmd.flora.view.authen.fragment.resetpass

import androidx.activity.addCallback
import com.cmd.flora.base.BaseFragment
import com.cmd.flora.databinding.FragmentResetPassDoneBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPassDoneFragment :
    BaseFragment<FragmentResetPassDoneBinding>(FragmentResetPassDoneBinding::inflate) {
    override fun initView() {
        super.initView()
        mActivity?.let {
            activity?.onBackPressedDispatcher?.addCallback(it) {
                activity?.finishAffinity()
            }
        }
    }

}