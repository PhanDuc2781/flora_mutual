package com.cmd.flora.data.network.request

import androidx.annotation.StringDef
import com.cmd.flora.data.network.request.Area.Companion.FUKUSHIMA
import com.cmd.flora.data.network.request.Area.Companion.GIFU

@StringDef(value = [GIFU, FUKUSHIMA], open = false)
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class Area {
    companion object {
        const val GIFU = "gifu"
        const val FUKUSHIMA = "fukushima"
    }
}

//data class NewLetterPageRequest(
//    @IntRange(from = 0) override val page: Int,
//    override val per_page: Int = FacilityPageRequest.PER_PAGE,
//    @Area val area: String? = null,
//) : Decodable, BasePageRequest