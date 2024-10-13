package com.cmd.flora.data.network.request

import com.cmd.flora.data.network.base.Decodable

data class ResetPasswordRequest(val code: String , val email: String , val password: String) : Decodable
