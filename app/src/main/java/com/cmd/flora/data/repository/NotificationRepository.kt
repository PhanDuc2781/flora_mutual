package com.cmd.flora.data.repository

import com.cmd.flora.data.network.base.APIRequest
import com.cmd.flora.data.network.getInformationResponse
import com.cmd.flora.data.network.request.FacilityPageRequest
import com.cmd.flora.data.network.request.InformationGenre
import com.cmd.flora.data.network.request.InformationPageRequest
import com.cmd.flora.data.network.request.InformationTarget
import com.cmd.flora.data.network.response.InformationResponse
import com.cmd.flora.data.network.response.NotificationResponse
import com.cmd.flora.data.network.response.extract
import javax.inject.Inject

interface NotificationRepository {

    suspend fun getNotifications(
        page: Int,
        @InformationTarget target: String,
        perPage: Int = FacilityPageRequest.PER_PAGE,
    ): Result<Pair<List<NotificationResponse>, Boolean>>
}

class NotificationRepositoryImpl @Inject constructor(private val apiRequest: APIRequest) :
    NotificationRepository {

    override suspend fun getNotifications(
        page: Int,
        target: String,
        perPage: Int
    ): Result<Pair<List<NotificationResponse>, Boolean>> = apiRequest.getInformationResponse(
        InformationPageRequest(page, per_page = perPage, genre = listOf(InformationGenre.News), target = target)
    ).map { it.extract(page) { mapToNotificationResponse() } }

    private fun InformationResponse.mapToNotificationResponse() =
        NotificationResponse(
            id = id,
            image = media_url,
            btnName = genre_display,
            message = title,
            description = content,
            date = published_at,
            isRead = is_read,
            url = url,
            target_id = target_id,
            type = type
        )

}