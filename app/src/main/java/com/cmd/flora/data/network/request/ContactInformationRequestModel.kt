package com.cmd.flora.data.network.request

import com.cmd.flora.data.network.base.Decodable

data class ContactInformationRequestModel(
    val user_id: String?,
    val genre: String?,
    val facility_id: String?,
    val name: String?,
    val name_kana: String?,
    val tel: String?,
    val email: String?,
    val postcode: String?,
    val prefecture: String?,
    val city: String?,
    val address: String?,
    val opinion: String?,
) : Decodable
