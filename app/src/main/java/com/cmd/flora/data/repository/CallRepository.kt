package com.cmd.flora.data.repository

import com.cmd.flora.data.network.base.APIRequest
import com.cmd.flora.data.network.getCall
import com.cmd.flora.data.network.request.TelPageRequest
import com.cmd.flora.data.network.response.CallResponse
import com.cmd.flora.data.network.response.extract
import javax.inject.Inject

interface CallRepository {
    suspend fun getTelPage(request: TelPageRequest): Result<Pair<List<CallResponse>, Boolean>>
}

class CallRepositoryImpl @Inject constructor(private val apiRequest: APIRequest) : CallRepository {
    override suspend fun getTelPage(
        request: TelPageRequest
    ): Result<Pair<List<CallResponse>, Boolean>> = apiRequest.getCall(request).map {
        it.extract(request.page)
    }

}