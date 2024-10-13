package com.cmd.flora.data.repository

import com.cmd.flora.application.storage
import com.cmd.flora.data.model.QRData
import com.cmd.flora.data.network.base.APIRequest
import com.cmd.flora.data.network.getPoint
import com.cmd.flora.data.network.loadSVG
import com.cmd.flora.data.network.response.PointResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface QRRepository {
    suspend fun getQRData(): Result<QRData?>
    suspend fun checkScore(id: Int): Result<PointResponse?>?
}

class QRRepositoryImpl @Inject constructor(private val apiRequest: APIRequest) : QRRepository {
    override suspend fun getQRData(): Result<QRData?> {
        val data = apiRequest.loadSVG(storage.userInfo?.member?.qrcode_url ?: "")
        return data.map { it?.let { it1 -> QRData(it1) } }
    }

    override suspend fun checkScore(id: Int): Result<PointResponse?> =
        withContext(Dispatchers.IO) {
            return@withContext apiRequest.getPoint(id)
        }
}