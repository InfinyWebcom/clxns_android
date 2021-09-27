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
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.text.NumberFormat
import java.util.*

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun ProgressBar.show() {
    visibility = View.VISIBLE
}

fun ProgressBar.hide() {
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
fun ImageView.loadImage(url:String){
    Glide.with(this).load(url).into(this)
}

fun Int.convertToCurrency():String{
    val amount = NumberFormat.getCurrencyInstance(Locale("en", "in"))
        .format(this)
    return amount.substringBefore('.')
}