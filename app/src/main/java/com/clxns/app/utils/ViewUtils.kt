package com.clxns.app.utils

import android.app.Activity
import android.app.ProgressDialog
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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.bumptech.glide.Glide
import com.clxns.app.R
import com.clxns.app.databinding.DialogCollectableAmountBinding
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

fun Context.toast(message : String) {
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

fun Context.hideKeyboard(view : View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.copyToClipBoard(text : CharSequence) {
    val clipboard = ContextCompat.getSystemService(this, ClipboardManager::class.java)
    val clip = ClipData.newPlainText("label", text)
    clipboard?.setPrimaryClip(clip)
}

fun View.snackBar(message : String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { snackBar ->
        snackBar.setAction("Ok") {
            snackBar.dismiss()
        }
    }.show()
}

fun String.isValidEmail() =
    !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.makeFirstLetterCapital() : String {
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

fun String.convertServerDateToNormal(newFormat : String, isGMT:Boolean = false) : String? {
    var date = this
    var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    spf.timeZone = if (isGMT) TimeZone.getTimeZone("GMT5:30+") else TimeZone.getDefault()
    var newDate : Date? = null
    try {
        newDate = spf.parse(date)
    } catch (e : ParseException) {
        e.printStackTrace()
    }
    spf = SimpleDateFormat(newFormat, Locale.getDefault())
    date = spf.format(newDate)
    return date
}

fun ImageView.loadImage(url : String) {
    Glide.with(this).load(url)
        .error(R.drawable.ic_logo_x)
        .placeholder(R.drawable.ic_logo_x)
        .into(this)
}

fun Int.convertToCurrency() : String {
    val amount = NumberFormat.getCurrencyInstance(Locale("en", "in"))
        .format(this)
    return amount.substringBefore('.')
}

fun View.showDialog(totalAmount : Int, collected : Int) {
    val dialog = MaterialAlertDialogBuilder(this.context)
    val c = DialogCollectableAmountBinding.inflate(LayoutInflater.from(this.context))
    dialog.setView(c.root)
        .setPositiveButton("OK") { d, _ ->
            d.dismiss()
        }
        .show()
    c.txtTotalDue.text = totalAmount.convertToCurrency()
    c.txtCollectable.text = collected.convertToCurrency()
    c.txtResult.text = (totalAmount.minus(collected)).convertToCurrency()
}

fun String.getDateInLongFormat() : Long {
    val spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    spf.timeZone = TimeZone.getDefault()
    val newDate : Date?
    var dateInLong = 0L
    try {
        newDate = spf.parse(this)
        dateInLong = newDate.time
    } catch (e : ParseException) {
        e.printStackTrace()
    }
    return dateInLong
}

@Throws(IOException::class)
fun File.getBase64StringFile() : String {
    var inputStream : InputStream? = null
    var encodedFile = ""
    try {
        inputStream = FileInputStream(this.absolutePath)
        val buffer = ByteArray(10240) //specify the size to allow
        var bytesRead : Int
        val output = ByteArrayOutputStream()
        val output64 = Base64OutputStream(output, Base64.DEFAULT)
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            output64.write(buffer, 0, bytesRead)
        }
        output64.close()
        encodedFile = output.toString()
    } catch (e1 : IOException) {
        e1.printStackTrace()
    } finally {
        inputStream?.close()
    }
    return encodedFile
}

fun Uri.getFileNameFromUri(context : Context) : String? {
    var filepath : String? = ""
    when {
        this.scheme.toString().compareTo("content") == 0 -> {
            val contentResolver = context.contentResolver ?: return null
            // Create file path inside app's data dir
            val filePath = (context.applicationInfo.dataDir + File.separator
                    + System.currentTimeMillis())
            val file = File(filePath)
            try {
                val inputStream = contentResolver.openInputStream(this) ?: return null
                val outputStream : OutputStream = FileOutputStream(file)
                val buf = ByteArray(1024)
                var len : Int
                while (inputStream.read(buf).also { len = it } > 0) outputStream.write(
                    buf,
                    0,
                    len
                )
                outputStream.close()
                inputStream.close()
            } catch (ignore : IOException) {
                return null
            }
            filepath = file.absolutePath
        }
        this.scheme!!.compareTo("file") == 0 -> {
            try {
                val file = File(URI(this.toString()))
                if (file.exists()) filepath = file.absolutePath
            } catch (e : URISyntaxException) {
                e.printStackTrace()
            }
        }
        else -> {
            filepath = this.path
        }
    }
    return filepath
}

fun Context.createImageFile() : File? {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val storageDir = externalCacheDir //getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    // Save a file: path for use with ACTION_VIEW intents
    return File.createTempFile(
        imageFileName,  /* prefix */
        ".jpg",  /* suffix */
        storageDir /* directory */
    )
}

fun getProgressDialog(context : Context, title : String, msg : String) : ProgressDialog{
    val pd = ProgressDialog(context)
    pd.setTitle(title)
    pd.setMessage(msg)
    pd.setCancelable(false)
    pd.create()
    return pd
}

fun getCalculatedDate(days : Int) : String {
    val calendar = Calendar.getInstance()
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    calendar.add(Calendar.DAY_OF_YEAR, days)
    return formatter.format(Date(calendar.timeInMillis))
}

//This ensure if the user has click a button multiple time to navigate to a different screen so it does
//not throws illegal argument exception error saying cannot find specified navigation action
fun NavController.safeNavigate(directions: NavDirections){
    currentDestination?.getAction(directions.actionId)?.run {
        navigate(directions)
    }
}