package com.cmd.flora.data.network.request

import com.cmd.flora.data.network.base.Decodable

data class LoginRequestModel(val member_code: String, val password: String) : Decodable {
    companion object {
        val mocks = listOf(
            LoginRequestModel("00001-0000-0001", "password"),
            LoginRequestModel("00001-0000-0002", "password"),
            LoginRequestModel("00001-0000-0003", "password")
        )
    }
}
