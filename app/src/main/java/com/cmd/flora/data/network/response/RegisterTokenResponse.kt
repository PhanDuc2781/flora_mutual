package com.cmd.flora.data.network.response

data class RegisterTokenResponse(
    val detail: RegisterTokenResponseDetail?,
    val status: Int?,
    val summary: String?,
    val title: String?
)

data class RegisterTokenResponseDetail(val message: String?)