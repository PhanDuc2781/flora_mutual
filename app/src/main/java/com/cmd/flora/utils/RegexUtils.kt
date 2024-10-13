package com.cmd.flora.utils

enum class ValidatePattern(val pattern: String) {
    //    ACCOUNT_ID("""^\d{4}-\d{2}-\d{7}$"""),
    ACCOUNT_ID("^[0-9]{6}\$"),
    PASSWORD("^[a-zA-Z0-9]{8,}\$"),
    EMAIL(android.util.Patterns.EMAIL_ADDRESS.pattern()),
    PHONE(android.util.Patterns.PHONE.pattern()),
    KATAKANA("^[ァ-ヴー]+\$"),
    POSTAL_CODE("^[0-9]{7}\$");
}

infix fun CharSequence.matches(regex: ValidatePattern): Boolean =
    isNotEmpty() && Regex(regex.pattern).matches(this)

fun CharSequence.isAccountID(): Boolean =
    isNotEmpty() && this matches ValidatePattern.ACCOUNT_ID

fun CharSequence.isPassword(): Boolean =
    isNotEmpty() && this matches ValidatePattern.PASSWORD

fun CharSequence.isEmail(): Boolean =
    isNotEmpty() && this matches ValidatePattern.EMAIL

fun CharSequence.isPhoneNumber(): Boolean =
    isNotEmpty() && this matches ValidatePattern.PHONE

fun CharSequence.isKatakana(): Boolean =
    isNotEmpty() && this matches ValidatePattern.KATAKANA

fun CharSequence.isPostalCode(): Boolean =
    isNotEmpty() && this matches ValidatePattern.POSTAL_CODE
