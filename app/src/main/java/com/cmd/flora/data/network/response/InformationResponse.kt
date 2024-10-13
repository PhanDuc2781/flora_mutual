package com.cmd.flora.data.network.response

data class InformationResponse(
    val id: String?,
    val title: String?,
    val content: String?,
    val url: String?,
    val media_url: String?,
    val published_at: String?,
    val is_read: Boolean?,
    val genre_display: String?,
    val user_id: String?,
    val admin_id: String?,
    var target_id: String? = null,
    var type: String? = null
)