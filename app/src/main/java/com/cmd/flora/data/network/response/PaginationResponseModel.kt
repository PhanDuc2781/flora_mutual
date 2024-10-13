package com.cmd.flora.data.network.response

import com.google.gson.annotations.SerializedName

data class PaginationResponseModel<T>(
    @SerializedName("meta") val meta: MetaResponseModel?,
    @SerializedName("data") val data: List<T>?
)

data class MetaResponseModel(
    @SerializedName("total") val total: Int?,
    @SerializedName("per_page") val perPage: Int?,
    @SerializedName("current_page") val currentPage: Int?,
    @SerializedName("last_page") val lastPage: Int?,
)

fun <T> PaginationResponseModel<T>?.getData(): List<T> = this?.data.orEmpty()

infix fun PaginationResponseModel<*>?.hasLoadMore(page: Int): Boolean =
    page < (this?.meta?.lastPage ?: (page + 1))

fun <T> PaginationResponseModel<T>?.extract(page: Int): Pair<List<T>, Boolean> =
    getData() to hasLoadMore(page)

fun <T, U> PaginationResponseModel<T>?.extract(
    page: Int,
    map: T.() -> U
): Pair<List<U>, Boolean> =
    getData().map { map(it) } to hasLoadMore(page)
