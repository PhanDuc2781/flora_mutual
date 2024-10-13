package com.cmd.flora.view.authen.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cmd.flora.application.deviceToken
import com.cmd.flora.application.deviceUUID
import com.cmd.flora.application.storage
import com.cmd.flora.base.BaseViewModel
import com.cmd.flora.data.network.base.APIRequest.Companion.isMock
import com.cmd.flora.data.network.base.HTTPError
import com.cmd.flora.data.network.request.LoginRequestModel
import com.cmd.flora.data.network.response.LoginResponseModel
import com.cmd.flora.data.network.response.MessageErrorRes
import com.cmd.flora.data.network.response.getAccessToken
import com.cmd.flora.data.repository.LoginRepository
import com.cmd.flora.data.repository.onFailureDecode
import com.cmd.flora.utils.MessageError
import com.cmd.flora.utils.SingleLiveEvent
import com.cmd.flora.utils.isAccountID
import com.cmd.flora.utils.isPassword
import com.cmd.flora.utils.orNull
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val gson: Gson
) : BaseViewModel() {

    val loginStateSuccess = SingleLiveEvent<Boolean>()

    val validateAccountId = MutableLiveData<String>()

    val validatePassword = MutableLiveData<String>()

    val mergeEdittext = MutableLiveData<Pair<String, String>>()

    val errorMessageRequest = SingleLiveEvent<String>()

    fun validateAccount(accountId: String, password: String) {
        when {
            !accountId.isAccountID() -> validateAccountId.postValue(
                MessageError.INVALID_ACCOUNT_ID_MESSAGE
            )

            !password.isPassword() -> validatePassword.postValue(
                MessageError.INVALID_PASSWORD_MESSAGE
            )

            else -> viewModelScope.launch {
                if (isMock && !LoginRequestModel.mocks.contains(
                        LoginRequestModel(
                            accountId,
                            password
                        )
                    )
                ) {
                    errorMessageRequest.postValue(MessageError.INVALID_ACCOUNT_ID_MESSAGE)
                    return@launch
                }
                login(accountId, password)
            }
        }
    }

    private suspend fun login(userId: String, password: String) {
        showProgress(true)
        try {
            loginRepository.login(userId, password).onSuccess {
                storage.accessToken = it?.getAccessToken() ?: ""
                registerToken()
            }.onFailure {
                handleErrorRequest(it)
            }.onFailureDecode<LoginResponseModel?, MessageErrorRes>(gson) {
                Log.d(this::class.simpleName, it?.errors?.message.toString())
            }
        } finally {
            showProgress(false)
        }
    }

    private suspend fun registerToken() {
        Pair(deviceUUID(), deviceToken()).orNull()?.let {
            val response = loginRepository.registerToken(it.first, it.second)

            response?.onSuccess {
                it?.let {
                    loginStateSuccess.postValue(true)
                }
            }?.onFailure { msg ->
                handleErrorRequest(msg)
            }
        } ?: handleErrorRequest(null)
    }


    private fun handleErrorRequest(errorMessage: Throwable?) {
        storage.accessToken = ""
        val defaultErrorMessage = MessageError.MSG_ERROR_LOGIN_REQUEST
        errorMessage?.let {
            when (HTTPError.from(it)?.code) {
                HTTPError.UNAUTHORISE.code -> {
<<<<<<< HEAD
                    errorMessageRequest.postValue(MessageError.MSG_ERROR_LOGIN_REQUEST)
                }

                HTTPError.BAD_REQUEST.code -> {
                    errorMessageRequest.postValue("")
=======
//                    errorMessageRequest.postValue("")
                    validatePassword.postValue(MessageError.INVALID_PASSWORD_MESSAGE)
                }

                HTTPError.BAD_REQUEST.code -> {
                    errorMessageRequest.postValue(MessageError.MSG_ERROR_LOGIN_REQUEST)
>>>>>>> 19f69cb82a1f2f5a2f30fa6c5f44172ba5fad5cc
                }
                //more code here

                else -> errorMessageRequest.postValue(defaultErrorMessage)
            }
        } ?: errorMessageRequest.postValue(defaultErrorMessage)
    }

    fun clearErrorText() {
        validatePassword.value = ""
        validateAccountId.value = ""
    }
}
