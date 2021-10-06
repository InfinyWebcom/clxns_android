package com.clxns.app.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.clxns.app.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.io.*
import java.net.URI
import java.net.URISyntaxException
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
        .placeholder(R.drawable.ic_logo_x)
        .into(this)
}

fun Int.convertToCurrency(): String {
    val amount = NumberFormat.getCurrencyInstance(Locale("en", "in"))
        .format(this)
    return amount.substringBefore('.')
}

fun View.showDialog(totalAmount: Int, collected: Int) {
    val dialog = MaterialAlertDialogBuilder(this.context)
    val customView = LayoutInflater.from(this.context)
        .inflate(R.layout.dialog_collectable_amount, null, false)
    val txtTotalDue = customView.findViewById<TextView>(R.id.txt_total_due)
    val txtCollected = customView.findViewById<TextView>(R.id.txt_collectable)
    val txtResult = customView.findViewById<TextView>(R.id.txt_result)
    dialog.setView(customView)
        .setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        .show()
    txtTotalDue.text = "₹${totalAmount}"
    txtCollected.text = "₹${collected}"
    txtResult.text = "₹${totalAmount.minus(collected)}"
}

fun String.formatDate(format: String, newFormat: String): String {
    var date: String = this
    var spf = SimpleDateFormat(format, Locale.getDefault())
    spf.timeZone = TimeZone.getTimeZone("UTC")
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

@Throws(IOException::class)
fun File.getBase64StringFile(): String {
    var inputStream: InputStream? = null
    var encodedFile = ""
    val lastVal: String
    try {
        inputStream = FileInputStream(this.absolutePath)
        val buffer = ByteArray(10240) //specify the size to allow
        var bytesRead: Int
        val output = ByteArrayOutputStream()
        val output64 = Base64OutputStream(output, Base64.DEFAULT)
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            output64.write(buffer, 0, bytesRead)
        }
        output64.close()
        encodedFile = output.toString()
    } catch (e1: IOException) {
        e1.printStackTrace()
    } finally {
        inputStream?.close()
    }
    lastVal = encodedFile
    return lastVal
}

fun Uri.getFileNameFromUri(context: Context): String? {
    var filepath: String? = ""
    when {
        this.scheme.toString().compareTo("content") == 0 -> {
            val contentResolver = context.contentResolver ?: return null
            // Create file path inside app's data dir
            val filePath = (context.applicationInfo.dataDir + File.separator
                    + System.currentTimeMillis())
            val file = File(filePath)
            try {
                val inputStream = contentResolver.openInputStream(this) ?: return null
                val outputStream: OutputStream = FileOutputStream(file)
                val buf = ByteArray(1024)
                var len: Int
                while (inputStream.read(buf).also { len = it } > 0) outputStream.write(
                    buf,
                    0,
                    len
                )
                outputStream.close()
                inputStream.close()
            } catch (ignore: IOException) {
                return null
            }
            filepath = file.absolutePath
        }
        this.scheme!!.compareTo("file") == 0 -> {
            try {
                val file = File(URI(this.toString()))
                if (file.exists()) filepath = file.absolutePath
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        }
        else -> {
            filepath = this.path
        }
    }
    return filepath
}

fun Context.createImageFile(): File? {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val storageDir = externalCacheDir //getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val image = File.createTempFile(
        imageFileName,  /* prefix */
        ".jpg",  /* suffix */
        storageDir /* directory */
    )
    // Save a file: path for use with ACTION_VIEW intents
    return image
}