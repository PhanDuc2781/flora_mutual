package com.cmd.flora.data.network.request

import androidx.annotation.IntRange
import com.cmd.flora.data.network.base.Decodable


data class ServiceHistoryPageRequest(
    @IntRange(from = 0) override val page: Int,
    override val per_page: Int = PER_PAGE,
) : Decodable, BasePageRequest {
    companion object {
        const val PER_PAGE = 20
    }
}