package com.clxns.app.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.clxns.app.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun TextInputEditText.removeFocus() {
    if (this.hasFocus()) {
        this.clearFocus()
    }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.copyToClipBoard(text: CharSequence) {
    val clipboard = ContextCompat.getSystemService(this, ClipboardManager::class.java)
    val clip = ClipData.newPlainText("label", text)
    clipboard?.setPrimaryClip(clip)
}

fun View.snackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { snackBar ->
        snackBar.setAction("Ok") {
            snackBar.dismiss()
        }
    }.show()
}

fun String.isValidEmail() =
    !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.makeFirstLetterCapital(): String {
    // stores each characters to a char array
    var foundSpace = true
    val arr = this.toCharArray()
    for (i in arr.indices) {
        if (foundSpace) {
            arr[i] = Character.toUpperCase(arr[i])
            foundSpace = false
        } else {
            foundSpace = arr[i] == ' '
        }
    }
    return String(arr)
}

fun String.convertServerDateToNormal(newFormat: String): String? {
    var date = this
    var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    spf.timeZone = TimeZone.getTimeZone("GMT")
    var newDate: Date? = null
    try {
        newDate = spf.parse(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    spf = SimpleDateFormat(newFormat, Locale.getDefault())
    date = spf.format(newDate)
    return date
}

fun ImageView.loadImage(url: String) {
    Glide.with(this).load(url)
        .error(R.drawable.ic_logo_x)
        .into(this)
}

fun Int.convertToCurrency(): String {
    val amount = NumberFormat.getCurrencyInstance(Locale("en", "in"))
        .format(this)
    return amount.substringBefore('.')
}