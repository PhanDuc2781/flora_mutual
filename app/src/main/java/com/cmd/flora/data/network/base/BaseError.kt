package com.cmd.flora.data.network.base

class BaseErrorRes<out E>(
    val title: String?,
    val summary: String?,
    val status: Int?,
    val errors: E?,
    val detail: DetailRes?
)

data class DetailRes(val message: String?)