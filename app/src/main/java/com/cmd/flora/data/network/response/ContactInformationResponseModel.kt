package com.cmd.flora.data.network.response

data class ContactInformationResponseModel(
    val data: Data?
)

data class Data(
    val id: String?,
    val user_id: String?,
    val genre: String?
)