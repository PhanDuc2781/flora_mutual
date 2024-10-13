package com.cmd.flora.data.network.request

import androidx.annotation.IntRange
import com.cmd.flora.data.network.base.Decodable
import com.cmd.flora.data.network.request.FacilityPageRequest.Companion.PER_PAGE

data class TelPageRequest(
    @IntRange(from = 0) override val page: Int,
    override val per_page: Int = PER_PAGE,
    @FacilityGenre val genre: String
) : Decodable, BasePageRequest