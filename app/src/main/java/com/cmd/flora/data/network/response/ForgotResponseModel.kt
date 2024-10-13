package com.cmd.flora.data.network.response


data class ForgotResponseModel(
    val title: String?,
    val summary: String?,
    val status: Int?,
    val detail: Detail?
)

data class Detail(val message: String?)
