package com.elapp.storyapp.utils.ext

import android.text.TextUtils

fun String.isEmailValid(): Boolean  {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.timeStamptoString(): String = substring(0, 10)

fun String.toBearerToken(): String {
    return "Bearer $this"
}