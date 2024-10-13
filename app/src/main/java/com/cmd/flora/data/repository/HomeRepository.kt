package com.cmd.flora.data.repository


import com.cmd.flora.data.network.base.APIRequest
import com.cmd.flora.data.network.getDetailInformation
import com.cmd.flora.data.network.getHomeNotify
import com.cmd.flora.data.network.getInformationResponse
import com.cmd.flora.data.network.getToast
import com.cmd.flora.data.network.getUserInfor
import com.cmd.flora.data.network.request.InformationGenre
import com.cmd.flora.data.network.request.InformationPageRequest
import com.cmd.flora.data.network.request.InformationTarget
import com.cmd.flora.data.network.response.HomeNotify
import com.cmd.flora.data.network.response.HomeNotifyResponse
import com.cmd.flora.data.network.response.InformationResponse
import com.cmd.flora.data.network.response.NewResponse
import com.cmd.flora.data.network.response.UserInforResponseModel
import com.cmd.flora.data.network.response.extract
import com.cmd.flora.data.network.response.mapToHomeNotify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface HomeRepository {

    suspend fun getToastMessage(): Result<HomeNotifyResponse?>?

    suspend fun getNewsHome(page: Int): Result<Pair<List<NewResponse>, Boolean>>

    suspend fun getNews1(page: Int): Result<Pair<List<NewResponse>, Boolean>>
    suspend fun getNews2(page: Int): Result<Pair<List<NewResponse>, Boolean>>
    suspend fun getNews3(page: Int, isPrivate: Boolean): Result<Pair<List<NewResponse>, Boolean>>
    suspend fun getUserInfor(): UserInforResponseModel?
    suspend fun getDetailInformation(
        idx: Int,
        shouldCheckError: Boolean = false
    ): InformationResponse?

    suspend fun getHomeNotify(): HomeNotify?
}

class HomeRepositoryImpl @Inject constructor(private val apiRequest: APIRequest) :
    HomeRepository {
    override suspend fun getHomeNotify(): HomeNotify? =
        withContext(Dispatchers.IO) {
            val response = apiRequest.getHomeNotify().getOrNull()
            return@withContext response?.mapToHomeNotify()
        }

    override suspend fun getToastMessage(): Result<HomeNotifyResponse?> {
        return apiRequest.getToast()
    }

    override suspend fun getNewsHome(page: Int): Result<Pair<List<NewResponse>, Boolean>> =
        apiRequest.getInformationResponse(
            InformationPageRequest(
                page = page,
                genre = listOf(
                    InformationGenre.Event,
                    InformationGenre.Product,
                    InformationGenre.Other
                )
            )
        ).map { it.extract(page) { mapToNewResponse() } }

    override suspend fun getNews1(page: Int): Result<Pair<List<NewResponse>, Boolean>> {
        return apiRequest.getInformationResponse(
            InformationPageRequest(
                page = page,
                genre = listOf(InformationGenre.Event)
            )
        ).map { it.extract(page) { mapToNewResponse() } }
    }

    override suspend fun getNews2(page: Int): Result<Pair<List<NewResponse>, Boolean>> {
        return apiRequest.getInformationResponse(
            InformationPageRequest(
                page = page,
                genre = listOf(InformationGenre.Product)
            )
        ).map { it.extract(page) { mapToNewResponse() } }
    }

    override suspend fun getNews3(
        page: Int,
        isPrivate: Boolean
    ): Result<Pair<List<NewResponse>, Boolean>> {
        return apiRequest.getInformationResponse(
            InformationPageRequest(
                page = page,
                genre = listOf(InformationGenre.News),
                target = if (isPrivate) InformationTarget.Oneself else InformationTarget.All
            )
        ).map { it.extract(page) { mapToNewResponse() } }
    }

    override suspend fun getUserInfor(): UserInforResponseModel? = withContext(Dispatchers.IO) {
        apiRequest.getUserInfor().getOrNull()
    }

    override suspend fun getDetailInformation(
        idx: Int,
        shouldCheckError: Boolean
    ): InformationResponse? =
        withContext(Dispatchers.IO) {
            apiRequest.getDetailInformation(idx, shouldCheckError).getOrNull()
        }


    private fun InformationResponse.mapToNewResponse(): NewResponse = NewResponse(
        id = this.id,
        image = this.media_url,
        displayGenre = this.genre_display,
        message = this.title,
        description = this.content,
        url = this.url,
        isPrivate = false,
        target_id = target_id,
        type = type
    )

}