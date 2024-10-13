package com.cmd.flora.data.network.response

data class UnReadNotificationResponse(
    val data: UnReadData?
)

data class UnReadData(val unread_count: Int?)