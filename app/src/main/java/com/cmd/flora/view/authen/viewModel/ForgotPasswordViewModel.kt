package com.cmd.flora.view.authen.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cmd.flora.base.BaseViewModel
import com.cmd.flora.data.network.base.BaseErrorRes
import com.cmd.flora.data.network.response.EmailErrors
import com.cmd.flora.data.network.response.ForgotResponseModel
import com.cmd.flora.data.repository.LoginRepository
import com.cmd.flora.data.repository.onFailureDecode
import com.cmd.flora.utils.MessageError
import com.cmd.flora.utils.SingleLiveEvent
import com.cmd.flora.utils.isEmail
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val loginRepository: LoginRepository, private val gson: Gson
) : BaseViewModel() {

    val errorFormatEmail = MutableLiveData<String>()

    val observerEdittext = MutableLiveData<String>()

    val errorMgs = SingleLiveEvent<BaseErrorRes<EmailErrors>>()

    val forgotPassRequestSuccess = SingleLiveEvent<ForgotResponseModel?>()

    fun validateEmail(email: String) {
        when {
            !email.isEmail() -> errorFormatEmail.postValue(MessageError.INVALID_EMAIL_MESSAGE)

            else -> {
                viewModelScope.launch {
                    sendForgotPasswordRequest(email)
                }
            }
        }
    }

    fun clearErrorTxt() {
        errorFormatEmail.value = ""
    }

    private suspend fun sendForgotPasswordRequest(email: String) {
        showProgress(true)
        try {
            loginRepository.forgotPassword(email).onSuccess {
                forgotPassRequestSuccess.postValue(it)
            }.onFailureDecode<ForgotResponseModel?, EmailErrors>(gson) {
                errorMgs.postValue(it)
            }
        } finally {
            showProgress(false)
        }
    }

}
