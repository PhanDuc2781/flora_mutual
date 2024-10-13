package com.cmd.flora.data.network.response

data class NewResponse(
    val id: String? = null,
    val image: String? = null,
    val displayGenre: String? = null,
    val message: String? = null,
    val description: String? = null,
    val url: String? = null,
    val isPrivate: Boolean = false,
    var target_id: String? = null,
    var type: String? = null
) {
    companion object {
        val emptyValue = NewResponse()
    }
}