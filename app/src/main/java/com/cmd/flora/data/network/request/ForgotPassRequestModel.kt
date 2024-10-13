package com.cmd.flora.data.network.request

import com.cmd.flora.data.network.base.Decodable

data class ForgotPassRequestModel(val email: String) : Decodable
