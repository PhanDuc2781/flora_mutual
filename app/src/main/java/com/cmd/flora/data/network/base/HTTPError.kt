package com.cmd.flora.data.network.base

<<<<<<< HEAD
=======
import com.cmd.flora.data.repository.OtherError

>>>>>>> 19f69cb82a1f2f5a2f30fa6c5f44172ba5fad5cc
enum class HTTPError(val code: Int) {
    UNAUTHORISE(401),
    BAD_REQUEST(400),
    NOT_FOUND(404),
    SERVER_ERROR(500),
    UNPROCESSABLE_CONTENT(422);

    companion object {
        fun get(code: Int) = entries.firstOrNull { it.code == code }

<<<<<<< HEAD
        fun from(throwable: Throwable) = throwable.httpCode()?.let { return@let get(it) }
=======
        fun from(throwable: Throwable) =
            if (throwable is OtherError) {
                throwable.throwable.httpCode()?.let { return@let get(it) }
            } else {
                throwable.httpCode()?.let { return@let get(it) }
            }

>>>>>>> 19f69cb82a1f2f5a2f30fa6c5f44172ba5fad5cc
    }

}