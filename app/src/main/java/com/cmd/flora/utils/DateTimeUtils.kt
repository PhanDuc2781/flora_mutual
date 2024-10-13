package com.cmd.flora.utils

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.RelativeSizeSpan
import com.cmd.flora.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Double.formatJapan(): String = NumberFormat.getNumberInstance(Locale.JAPAN).format(this)

fun getScoreText(
    context: Context,
    score: Double, relative1: Float, relative2: Float
): CharSequence {
    var scoreString = score.formatJapan()
    if (scoreString == "0") scoreString = "0.000"
    val text2 = " ${context.getString(R.string.pt)}"
    val span1 = SpannableString(scoreString)
    span1.setSpan(
        RelativeSizeSpan(relative1),
        0,
        scoreString.length,
        Spanned.SPAN_INCLUSIVE_INCLUSIVE
    )

    val span2 = SpannableString(text2)
    span2.setSpan(
        RelativeSizeSpan(relative2),
        0,
        text2.length,
        Spanned.SPAN_INCLUSIVE_INCLUSIVE
    )

    return TextUtils.concat(span1, span2)
}

fun getScoreText(
    context: Context,
    score: Double, size1: Int, size2: Int
): CharSequence {
    var scoreString = score.formatJapan()
    if (scoreString == "0") scoreString = "0.000"
    val text2 = " ${context.getString(R.string.pt)}"
    val span1 = SpannableString(scoreString)
    span1.setSpan(
        AbsoluteSizeSpan(size1.cv.dpToPx()),
        0,
        scoreString.length,
        Spanned.SPAN_INCLUSIVE_INCLUSIVE
    )

    val span2 = SpannableString(text2)
    span2.setSpan(
        AbsoluteSizeSpan(size2.cv.dpToPx()),
        0,
        text2.length,
        Spanned.SPAN_INCLUSIVE_INCLUSIVE
    )

    return TextUtils.concat(span1, span2)
}

enum class DateFormat(val pattern: String) {
    FULL_DATE_TIME("yyyy-MM-dd HH:mm:ss"),
    FULL_DATE_API("yyyy-MM-dd"),
    FULL_DATE_1("yyyy.MM.dd"),
    FULL_DATE_2("yyyy.MM.dd発行"),
    FULL_DATE("yyyy/MM/dd"),
    FULL_TIME("HH:mm:ss"),
    FULL_DATE_JP("yyyy年MM月dd日"),
    FULL_DATE_ORDER("yyyy/MM/dd HH:mm:ss")
}

fun String.dateFormat(format: Pair<DateFormat, DateFormat>): String {
    return this.dateFormat(format.first, format.second)
}

fun String.dateFormat(fromPattern: DateFormat, toPattern: DateFormat): String {
    return this.dateFormat(fromPattern.pattern, toPattern.pattern)
}

fun String.dateFormat(fromPattern: String, toPattern: String): String {
    return this.toDate(fromPattern)?.toString(toPattern) ?: ""
}

fun String.toDate(pattern: String): Date? {
    return try {
        val sdf = SimpleDateFormat(pattern, Locale.JAPAN)
        sdf.parse(this)
    } catch (e: Exception) {
        null
    }
}

fun Date.toString(pattern: String): String? {
    return try {
        val sdf = SimpleDateFormat(pattern, Locale.JAPAN)
        sdf.format(this)
    } catch (e: Exception) {
        return null
    }
}