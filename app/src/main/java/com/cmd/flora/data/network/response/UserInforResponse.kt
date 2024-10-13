package com.cmd.flora.data.network.response

import com.cmd.flora.base.StorageEncodable

data class UserInforResponseModel(
    val data: UserData?
)

@StorageEncodable
data class UserData(
    val member_code: String? = null,
    val name: String? = null,
    val email: String? = null,
    val member: Member? = null,
    var is_push_notification_enabled: Int? = null
)

data class Member(
    val member_code: String? = null,
    val point: String? = null,
    val rank: String? = null,
    val rank_display: String? = null,
    val contract: Contract? = null,
    val payment_count_in_month: Int? = null,
    val qrcode_url: String? = null,
    val latest_earned_point: String? = null
)

data class Contract(
    val premium_payment_number: String? = null,
    val expected_completion_date: String? = null
)


