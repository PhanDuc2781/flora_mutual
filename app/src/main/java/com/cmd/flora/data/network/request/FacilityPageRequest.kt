package com.cmd.flora.data.network.request

import androidx.annotation.IntRange
import androidx.annotation.StringDef
import com.cmd.flora.data.network.base.Decodable

import com.cmd.flora.data.network.request.FacilityGenre.Companion.Benefit
import com.cmd.flora.data.network.request.FacilityGenre.Companion.Funeral
import com.cmd.flora.data.network.request.FacilityGenre.Companion.Shop
import com.cmd.flora.data.network.request.FacilityGenre.Companion.Wedding
import com.cmd.flora.data.network.request.FacilityPageRequest.PageOrder.Companion.Order1
import com.cmd.flora.data.network.request.FacilityPageRequest.PageOrder.Companion.Order2
import com.cmd.flora.data.network.request.Prefecture.Companion.Fukushima
import com.cmd.flora.data.network.request.Prefecture.Companion.Gifu
import com.cmd.flora.data.network.request.Prefecture.Companion.Miyagi
import com.cmd.flora.data.network.request.Sort.Companion.ASC
import com.cmd.flora.data.network.request.Sort.Companion.DESC


interface BasePageRequest {
    val page: Int;
    val per_page: Int
}

@StringDef(value = [ASC, DESC], open = false)
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class Sort {
    companion object {
        const val ASC = "ASC"
        const val DESC = "DESC"
    }
}

@StringDef(value = [Funeral, Wedding, Benefit, Shop], open = false)
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class FacilityGenre {
    companion object {
        const val Funeral = "funeral"
        const val Wedding = "wedding"
        const val Benefit = "benefit"
        const val Shop = "shop"
    }
}

@StringDef(value = [Fukushima, Miyagi, Gifu], open = false)
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class Prefecture {
    companion object {
        const val Fukushima = "福島県"
        const val Miyagi = "宮城県"
        const val Gifu = "岐阜県"
    }
}

data class FacilityPageRequest(
    @IntRange(from = 0) override val page: Int,
    override val per_page: Int = PER_PAGE,
    @Prefecture val prefecture: String? = null,
    @Sort val sort: String? = null,
    @PageOrder val order: String? = null,
    @FacilityGenre val genre: String
) : Decodable, BasePageRequest {
    @StringDef(value = [Order1, Order2], open = false)
    @Target(AnnotationTarget.FIELD)
    @Retention(AnnotationRetention.SOURCE)
    annotation class PageOrder {
        companion object {
            const val Order1 = "ASC"
            const val Order2 = "DESC"
        }
    }

    companion object {
        const val PER_PAGE = 10
    }
}