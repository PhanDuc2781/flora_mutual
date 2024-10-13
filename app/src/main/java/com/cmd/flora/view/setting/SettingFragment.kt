package com.cmd.flora.view.setting

import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.cmd.flora.BuildConfig
import com.cmd.flora.R
import com.cmd.flora.application.storage
import com.cmd.flora.base.BaseVMFragment
import com.cmd.flora.base.BaseViewModel
import com.cmd.flora.base.setOnSingleClickListener
import com.cmd.flora.base.showCustomAlertDialog
import com.cmd.flora.base.userId
import com.cmd.flora.data.network.base.APIRequest
import com.cmd.flora.data.network.logout
import com.cmd.flora.data.network.togglePushNotification
import com.cmd.flora.data.network.withdraw
import com.cmd.flora.databinding.FragmentSettingBinding
import com.cmd.flora.utils.SingleLiveEvent
import com.cmd.flora.view.dialog.AlertData
import com.cmd.flora.view.fullscreenalert.FullScreenAlertActivity
import com.cmd.flora.view.main.MainActivity
import com.cmd.flora.view.main.logOut
import com.cmd.flora.view.main.userInformation
import com.cmd.flora.view.webview.WebViewActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment :
    BaseVMFragment<FragmentSettingBinding, SettingViewModel>(FragmentSettingBinding::inflate) {
    override val viewModel: SettingViewModel by viewModels()

    companion object {
        const val COMPANY_URL = "https://www.flora-g.co.jp/company"
        const val LICENCE_URL = "https://www.flora-g.co.jp/licence"
    }

    override fun initView() {
        super.initView()

        binding.btnCompanyProfile.setOnSingleClickListener {
            mActivity?.let { it1 -> WebViewActivity.start(it1, COMPANY_URL) }
        }

        binding.btnLicense.setOnSingleClickListener {
            mActivity?.let { it1 -> WebViewActivity.start(it1, LICENCE_URL) }
        }

        binding.btnLogout.setOnSingleClickListener {
            mActivity?.showCustomAlertDialog(AlertData(
                getString(R.string.sureLogoutTitleQuest),
                getString(R.string.logoutMsgAlert),
                getString(R.string.yesTitle),
                getString(R.string.noTitle)
            ) {
                if (it) viewModel.logoutAccount()
            })
        }

        binding.btnPrivacy.setOnSingleClickListener {
            mActivity?.let { it1 -> WebViewActivity.start(it1, BuildConfig.Privacy_URL) }
        }

        viewModel.isNotificationEnabled.observe(this) {
            binding.switchNotify.setChecked(it.first, it.second)
        }

        viewModel.userId.observe(this) {
            binding.tvUserId.text = it
        }

        userInformation?.map { it.is_push_notification_enabled }?.observe(this) {
            viewModel.setNotificationEnabled(it == 1)
        }

        viewModel.onLogoutAccountSuccess.observe(this) {
            if (!it) return@observe
            storage.logout()
            FullScreenAlertActivity.start(
                mActivity, FullScreenAlertActivity.Data(
                    getString(R.string.logoutSuccessTitle),
                    getString(R.string.logoutSuccessMsgTitle),
                    getString(R.string.returnHomeTitle),
                    false
                )
            ) {
                mActivity?.logOut()
            }
        }

        viewModel.onDeleteAccountSuccess.observe(this) {
            if (!it) return@observe
            storage.logout()
            FullScreenAlertActivity.start(
                mActivity, FullScreenAlertActivity.Data(
                    getString(R.string.withdrawSuccessTitle),
                    getString(R.string.withdrawSuccessMsgTitle),
                    getString(R.string.returnHomeTitle),
                    false
                )
            ) {
                mActivity?.logOut()
            }
        }

        binding.btnWithdraw.setOnSingleClickListener {
            mActivity?.showCustomAlertDialog(AlertData(
                getString(R.string.areSureCancelQues),
                getString(R.string.withdrawMessageTitle),
                getString(R.string.yesTitle),
                getString(R.string.noTitle)
            ) {
                if (it) viewModel.deleteAccount()
            })
        }


        binding.switchNotify.setOnCheckedChangeListener {
            viewModel.setEnableNotify(it)
        }
    }

    override fun onResume() {
        super.onResume()
        (mActivity as? MainActivity)?.viewModel?.getUserInformation()
    }
}

@HiltViewModel
class SettingViewModel @Inject constructor(val apiRequest: APIRequest) : BaseViewModel() {

    val isNotificationEnabled = MutableLiveData(
        Pair(
            (storage.userInfo?.is_push_notification_enabled == 1),
            false
        )
    )
    val userId = MutableLiveData(storage.userId)

    val onDeleteAccountSuccess = SingleLiveEvent<Boolean>()
    val onLogoutAccountSuccess = SingleLiveEvent<Boolean>()

    fun setEnableNotify(isEnable: Boolean) {
        showProgress(true)
        viewModelScope.launch {
            val response = apiRequest.togglePushNotification(isEnable)
            if (response.getOrNull() == true) {
                setNotificationEnabled(isEnable)
                val newInfo = storage.userInfo
                newInfo?.is_push_notification_enabled = if (isEnable) 1 else 0
                storage.userInfo = newInfo
            } else {
                setNotificationEnabled(!isEnable)
            }
        }
    }

    fun setNotificationEnabled(enabled: Boolean) {
        isNotificationEnabled.postValue(enabled to false)
        showProgress(false)
    }

    fun logoutAccount() {
        showProgress(true)
        viewModelScope.launch {
            val response = apiRequest.logout()
            if (response.getOrNull() == true) {
                showProgress(false)
                onLogoutAccountSuccess.postValue(true)
            } else {
                showProgress(false)
                onLogoutAccountSuccess.postValue(false)
            }
        }
    }

    fun deleteAccount() {
        showProgress(true)
        viewModelScope.launch {
            if (apiRequest.withdraw().getOrNull() == true) {
                showProgress(false)
                onDeleteAccountSuccess.postValue(true)
            } else {
                onDeleteAccountSuccess.postValue(false)
            }
        }
    }
}