package com.cmd.flora.view.authen.viewModel

import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cmd.flora.base.BaseViewModel
import com.cmd.flora.data.network.base.BaseErrorRes
import com.cmd.flora.data.network.response.ResetPasswordResponse
import com.cmd.flora.data.repository.LoginRepository
import com.cmd.flora.data.repository.onFailureDecode
import com.cmd.flora.utils.MessageError
import com.cmd.flora.utils.SingleLiveEvent
import com.cmd.flora.utils.isPassword
import com.cmd.flora.utils.textFlow
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val gson: Gson
) :
    BaseViewModel() {

    val mergerEdittext = MutableLiveData<Pair<String, String>>()
    val errorMessageRequest = SingleLiveEvent<BaseErrorRes<ResetPasswordErrorRequest?>>()
    val errorMessageValid = MutableLiveData<String>()
    val resetPasswordResponse = SingleLiveEvent<ResetPasswordResponse?>()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun mergerEdittext(editText1: EditText, editText2: EditText) = viewModelScope.launch {
        editText1.textFlow.combine(editText2.textFlow) { text1, text2 ->
            Pair(text1, text2)
        }.collect {
            mergerEdittext.postValue(it)
        }
    }

    fun validateNewPass(
        newPass: String,
        confirmNewPass: String,
        code: String,
        email: String
    ) {
        when {
            !(newPass.isPassword() && confirmNewPass.isPassword()) -> {
                errorMessageValid.value = MessageError.MSG_VALID_RESET_PASSWORD
            }

            newPass != confirmNewPass -> {
                errorMessageValid.value = MessageError.MSG_PASSWORD_NOT_MATCH
            }

            else -> {
                registerNewPass(code, email, newPass)
            }
        }


    }

    private fun registerNewPass(code: String, email: String, password: String) {
        showProgress(true)
        viewModelScope.launch {
            try {
                loginRepository.resetPassword(code, email, password)?.onSuccess {
                    resetPasswordResponse.postValue(it)
                }?.onFailureDecode<ResetPasswordResponse?, ResetPasswordErrorRequest>(gson) {
                    errorMessageRequest.postValue(it)
                }
            } finally {
                showProgress(false)
            }
        }
    }
}

data class ResetPasswordErrorRequest(val message: String)