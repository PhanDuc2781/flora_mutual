package com.cmd.flora.data.repository

import com.cmd.flora.data.model.ServiceHistory
import com.cmd.flora.data.network.base.APIRequest
import com.cmd.flora.data.network.getDetailInformation
import com.cmd.flora.data.network.getServiceHistory
import com.cmd.flora.data.network.request.InformationTarget
import com.cmd.flora.data.network.request.ServiceHistoryPageRequest
import com.cmd.flora.data.network.response.InformationResponse
import com.cmd.flora.data.network.response.NotificationResponse
import com.cmd.flora.data.network.response.extract
import com.cmd.flora.data.network.response.mapToServiceHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface MyPageRepository {
    suspend fun getMessageNotice(perPage: Int = 10): Result<List<NotificationResponse>>
    suspend fun getServiceHistory(page: Int): Result<Pair<List<ServiceHistory>, Boolean>>
    suspend fun getDetailInformation(idx: Int): InformationResponse?
}


class MyPageRepositoryImpl @Inject constructor(
    private val apiRequest: APIRequest,
    private val notificationRepository: NotificationRepository
) : MyPageRepository {

    override suspend fun getMessageNotice(perPage: Int): Result<List<NotificationResponse>> =
        notificationRepository.getNotifications(1, InformationTarget.Oneself, perPage = perPage).map {
            return@map it.first.take(perPage)
        }

    override suspend fun getServiceHistory(page: Int): Result<Pair<List<ServiceHistory>, Boolean>> =
        apiRequest.getServiceHistory(
            ServiceHistoryPageRequest(
                page = page
            )
        ).map { it.extract(page) { mapToServiceHistory() } }

    override suspend fun getDetailInformation(idx: Int): InformationResponse? =
        withContext(Dispatchers.IO) {
            apiRequest.getDetailInformation(idx).getOrNull()
        }
}