package com.cmd.flora.data.network.response


data class FacilityInformation(
    val id: Int?,
    val facility_code: String?,
    val name: String?,
    val zip: String?,
    val description: String?,
    val prefecture: String?,
    val address: String?,
    val tel: String?,
    val genre: String?,
    val genre_display: String?,
    val tags: List<Tag>?,
    val image: String?,
    val url: String?,
    val created_at: String?,
    val updated_at: String?
)

data class Tag(
    val id: Int?,
    val name: String?,
    val type: String?
)

