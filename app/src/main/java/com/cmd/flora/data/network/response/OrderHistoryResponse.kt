package com.cmd.flora.data.network.response

import com.cmd.flora.data.model.ServiceHistory


data class OrderHistoryResponse(
    val id: Int,
    val order_date: String?,
    val name: String?,
    val price: Double? = null
)

fun OrderHistoryResponse.mapToServiceHistory(): ServiceHistory = ServiceHistory(
    id = this.id,
    message = this.name,
    product = null,
    price = this.price,
    date = this.order_date
)
