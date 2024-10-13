package com.cmd.flora.data.repository

import com.cmd.flora.data.network.response.Prefecture
import com.cmd.flora.data.network.base.APIRequest
import com.cmd.flora.data.network.getContactFacilityList
import com.cmd.flora.data.network.getDataInquiry
import com.cmd.flora.data.network.request.ContactInformationRequest
import com.cmd.flora.data.network.request.ContactInformationRequestModel
import com.cmd.flora.data.network.response.ContactGenreResponse
import com.cmd.flora.data.network.response.ContactInformationResponseModel
import com.cmd.flora.data.network.response.getData
import com.cmd.flora.data.network.sendContact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface InquiryRepository {


    suspend fun getDataInquiry(): Pair<List<ContactGenreResponse>, List<Prefecture>>
    suspend fun getContacts(contactValue: String): List<ContactGenreResponse>
    suspend fun sendContact(dataRequest: ContactInformationRequestModel): Result<ContactInformationResponseModel?>?
}

class InquiryRepositoryImpl @Inject constructor(private val apiRequest: APIRequest) :
    InquiryRepository {

    override suspend fun getDataInquiry(): Pair<List<ContactGenreResponse>, List<Prefecture>> =
        withContext(Dispatchers.IO) {
            val response = apiRequest.getDataInquiry().getOrNull()
            val first = response?.first?.data
            val second = response?.second?.data
            return@withContext Pair(first.orEmpty(), second.orEmpty())
        }

    override suspend fun getContacts(contactValue: String): List<ContactGenreResponse> =
        withContext(Dispatchers.IO) {
            val response = apiRequest.getContactFacilityList(
                ContactInformationRequest(contactValue)
            ).getOrNull()
            return@withContext response.getData()
        }

    override suspend fun sendContact(dataRequest: ContactInformationRequestModel) = withContext(Dispatchers.IO) {
        return@withContext apiRequest.sendContact(dataRequest)
    }
}