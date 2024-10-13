package com.cmd.flora.data.network.response


data class PointResponseData(
    val id: Int,
    val admin_id: Int? = null,
    val member_code: String? = null,
    val relative: Double? = null,
    val absolute: Double? = null,
    val note: String? = null,
    val sub_point: Double? = null,
    val add_point: Double? = null,
    val updated_at: String? = null,
    val facility_name: String? = null
)

data class PointResponse(val data: PointResponseData?)
