package com.cmd.flora.data.network.response

import com.cmd.flora.base.AdapterEquatable

data class NotificationResponse(
    val id: String?,
    val image: String?,
    val btnName: String?,
    val message: String?,
    val description: String?,
    val date: String?,
    var isRead: Boolean?,
    var url: String? = null,
    var target_id: String? = null,
    var type: String? = null
) : AdapterEquatable