package com.cmd.flora.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.doAfterTextChanged
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import com.cmd.flora.R
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.nio.ByteBuffer

infix fun ImageView.load(url: Any?) = coilLoad(url) {
    placeholder(createPlaceholder(context))
    error(R.drawable.error)
}

fun ImageView.load(
    url: Any?,
    builder: ImageRequest.Builder.() -> Unit = { }
) = coilLoad(url) {
    placeholder(createPlaceholder(context))
    error(R.drawable.error)
    builder.invoke(this)
}

infix fun ImageView.loadLocal(url: Any?) = coilLoad(url)

infix fun ImageView.loadSVG(qrData: String) =
    load(ByteBuffer.wrap(qrData.toByteArray()), context.imageLoaderSVG)

inline val Context.imageLoaderSVG: ImageLoader
    get() = ImageLoader.Builder(this)
        .components { add(SvgDecoder.Factory()) }
        .respectCacheHeaders(false)
        .build()

inline fun ImageView.coilLoad(
    data: Any?,
    imageLoader: ImageLoader = context.imageLoader,
    builder: ImageRequest.Builder.() -> Unit = { }
) {
    val request = ImageRequest.Builder(context)
        .data(data)
        .target(this)
    builder.invoke(request)
    imageLoader.enqueue(request.build())
}

fun createPlaceholder(context: Context) = CircularProgressDrawable(context).also {
    it.strokeWidth = 5f
    it.centerRadius = 30f
    it.setColorSchemeColors(context.getColor(R.color.tab_selected_color))
    it.start()
}

val EditText.textFlow: Flow<String>
    get() = callbackFlow {
        val textWatcher = doAfterTextChanged { trySend(it.toString()).isSuccess }
        awaitClose { removeTextChangedListener(textWatcher) }
    }

fun String.splitStringWhitespace(): Pair<String, String> {
    var newValue = ""
    for (char in this) {
        if (char.isWhitespace()) {
            newValue += "|||"
        } else {
            newValue += char
        }
    }

    val lists = newValue.split("|||")

    return Pair(lists.firstOrNull() ?: "", lists.getOrNull(1) ?: "")
}
