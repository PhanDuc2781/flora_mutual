package com.cmd.flora.data.network.request

import com.cmd.flora.data.network.base.Decodable


data class RegisterTokenRequest(
    val device_uuid: String,
    val device_token: String,
    val device_type: Int = DEVICE_TYPE
) : Decodable {
    companion object {
        const val DEVICE_TYPE = 1
    }
}
