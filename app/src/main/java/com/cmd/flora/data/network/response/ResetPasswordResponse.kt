package com.cmd.flora.data.network.response

data class ResetPasswordResponse(
    val title: String?,
    val summary: String?,
    val status: String?,
    val detail: DetailResponseResetPass?
)
data class DetailResponseResetPass(val message: String?)