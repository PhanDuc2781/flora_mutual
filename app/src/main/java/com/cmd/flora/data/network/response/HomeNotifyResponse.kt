package com.cmd.flora.data.network.response

data class HomeNotifyResponse(
    val id: String? = null,
    val title: String? = null,
    val message: String? = null,
    val latest_information: NewResponse? = null,
    val unread_count: Int? = null
)

data class HomeNotify(
    val id: String? = null,
    val title: String? = null,
    val message: String? = null,
    val type: String? = null,
    val target_id: String? = null,
    val url: String? = null,
    val unreadCount: Int? = null
)

fun HomeNotifyResponse.mapToHomeNotify(): HomeNotify =
    HomeNotify(
        id = latest_information?.id,
        title = title,
        message = message,
        latest_information?.type,
        latest_information?.target_id,
        latest_information?.url,
        unreadCount = unread_count
    )

