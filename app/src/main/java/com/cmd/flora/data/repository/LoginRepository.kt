package com.cmd.flora.data.repository

import com.cmd.flora.data.network.base.APIRequest
import com.cmd.flora.data.network.forgotPass
import com.cmd.flora.data.network.login
import com.cmd.flora.data.network.registerToken
import com.cmd.flora.data.network.request.ForgotPassRequestModel
import com.cmd.flora.data.network.request.LoginRequestModel
import com.cmd.flora.data.network.request.RegisterTokenRequest
import com.cmd.flora.data.network.request.ResetPasswordRequest
import com.cmd.flora.data.network.resetPassword
import com.cmd.flora.data.network.response.ForgotResponseModel
import com.cmd.flora.data.network.response.LoginResponseModel
import com.cmd.flora.data.network.response.RegisterTokenResponse
import com.cmd.flora.data.network.response.ResetPasswordResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface LoginRepository {
    suspend fun login(accountId: String, password: String): Result<LoginResponseModel?>
    suspend fun forgotPassword(email: String): Result<ForgotResponseModel?>
    suspend fun resetPassword(
        code: String,
        email: String,
        newPass: String
    ): Result<ResetPasswordResponse?>?
    
    suspend fun registerToken(
        device_uuid: String,
        device_token: String
    ): Result<RegisterTokenResponse?>?
}

class LoginRepositoryImpl @Inject constructor(private val apiRequest: APIRequest) :
    LoginRepository {
    override suspend fun login(accountId: String, password: String): Result<LoginResponseModel?> =
        withContext(Dispatchers.IO) {
            apiRequest.login(LoginRequestModel(accountId, password))
        }


    override suspend fun forgotPassword(email: String): Result<ForgotResponseModel?> =
        withContext(Dispatchers.IO) {
            apiRequest.forgotPass(ForgotPassRequestModel(email))
        }


    override suspend fun resetPassword(
        code: String,
        email: String,
        newPass: String
    ): Result<ResetPasswordResponse?> = withContext(Dispatchers.IO) {
        apiRequest.resetPassword(ResetPasswordRequest(code, email, newPass))
    }

    override suspend fun registerToken(
        device_uuid: String,
        device_token: String
    ): Result<RegisterTokenResponse?> = withContext(Dispatchers.IO) {
        apiRequest.registerToken(RegisterTokenRequest(device_uuid, device_token))
    }
}