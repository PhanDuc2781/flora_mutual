package com.cmd.flora.data.network.base

import com.cmd.flora.data.repository.OtherError
enum class HTTPError(val code: Int) {
    UNAUTHORISE(401),
    BAD_REQUEST(400),
    NOT_FOUND(404),
    SERVER_ERROR(500),
    UNPROCESSABLE_CONTENT(422);

    companion object {
        fun get(code: Int) = entries.firstOrNull { it.code == code }


        fun from(throwable: Throwable) =
            if (throwable is OtherError) {
                throwable.throwable.httpCode()?.let { return@let get(it) }
            } else {
                throwable.httpCode()?.let { return@let get(it) }
            }
    }

}