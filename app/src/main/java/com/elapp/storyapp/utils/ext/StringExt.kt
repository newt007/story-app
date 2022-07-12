package com.elapp.storyapp.utils.ext

import android.text.TextUtils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.isEmailValid(): Boolean  {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.timeStamptoString(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    val date = sdf.parse(this) as Date

    return DateFormat.getDateInstance(DateFormat.FULL).format(date)
}

fun String.toBearerToken(): String {
    return "Bearer $this"
}