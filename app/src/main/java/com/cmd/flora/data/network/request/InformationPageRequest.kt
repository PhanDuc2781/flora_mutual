package com.cmd.flora.data.network.request

import androidx.annotation.IntRange
import androidx.annotation.StringDef
import com.cmd.flora.data.network.base.Decodable
import com.cmd.flora.data.network.request.InformationGenre.Companion.Event
import com.cmd.flora.data.network.request.InformationGenre.Companion.News
import com.cmd.flora.data.network.request.InformationGenre.Companion.Other
import com.cmd.flora.data.network.request.InformationGenre.Companion.Product
import com.cmd.flora.data.network.request.InformationTarget.Companion.All
import com.cmd.flora.data.network.request.InformationTarget.Companion.Oneself

@StringDef(value = [Event, News, Product, Other], open = false)
@Target(AnnotationTarget.FIELD, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.SOURCE)
annotation class InformationGenre {
    companion object {
        const val Event = "event"
        const val News = "news"
        const val Product = "product"
        const val Other = "other"
    }
}

@StringDef(value = [Oneself, All], open = false)
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class InformationTarget {
    companion object {
        const val Oneself = "oneself"
        const val All = "all"
    }
}

data class InformationPageRequest(
    @IntRange(from = 0) override val page: Int,
    override val per_page: Int = FacilityPageRequest.PER_PAGE,
    val genre: List<@InformationGenre String>? = null,
    @InformationTarget val target: String? = null
) : Decodable, BasePageRequest

fun InformationPageRequest.toRequest(): HashMap<String, Any> {
    val response = hashMapOf<String, Any>()
    response["page"] = page
    response["per_page"] = per_page
    genre?.let {
        genre.forEachIndexed { index, s ->
            response["genre[${index}]"] = s
        }
    }
    target?.let { response["target"] = it }
    return response
}

data class ContactInformationRequest(
    val genre: String? = null,
) : Decodable