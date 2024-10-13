package com.cmd.flora.utils

import android.content.res.Resources
import android.util.TypedValue

class Converter(val value: Number)

inline val Number.cv: Converter
    get() = Converter(this)

fun Converter.pxToDp(): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value.toFloat(),
        Resources.getSystem().displayMetrics
    )
}

fun Converter.pxToSp(): Float {
    val scaledDensity = Resources.getSystem().displayMetrics.scaledDensity
    return this.value.toFloat() / scaledDensity
}

fun Converter.dpToPx(): Int {
    return (value.toFloat() * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
}