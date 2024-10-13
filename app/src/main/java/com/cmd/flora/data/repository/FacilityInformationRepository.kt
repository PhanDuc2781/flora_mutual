package com.cmd.flora.data.repository

import com.cmd.flora.data.network.response.FacilityInformation
import com.cmd.flora.data.network.response.Prefecture
import com.cmd.flora.data.network.base.APIRequest
import com.cmd.flora.data.network.getFacilityInformation
import com.cmd.flora.data.network.request.FacilityPageRequest
import com.cmd.flora.data.network.response.extract
import javax.inject.Inject

interface FacilityInformationRepository {
    suspend fun getFacilityPrefecture(
        facilityPageRequest: FacilityPageRequest
    ): Result<Pair<List<FacilityInformation>, Boolean>>

    suspend fun getPrefecture(): List<Prefecture>
}

class FacilityInformationRepositoryImpl @Inject constructor(private val apiRequest: APIRequest) :
    FacilityInformationRepository {
    override suspend fun getFacilityPrefecture(
        facilityPageRequest: FacilityPageRequest
    ): Result<Pair<List<FacilityInformation>, Boolean>> = apiRequest.getFacilityInformation(
        facilityPageRequest
    ).map { it.extract(facilityPageRequest.page) }

    override suspend fun getPrefecture(): List<Prefecture> {
        return listOf(
            Prefecture(label = "Fukushima", value = "福島県"),
            Prefecture(label = "Miyagi", value = "宮城県"),
            Prefecture(label = "Gifu", value = "岐阜県")
        )
    }
}