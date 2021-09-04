package com.clxns.app.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class Utilities {
    companion object {
        fun hideKeyboardFrom(context: Context, view: View) {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun copyToClipboard(context: Context, text: CharSequence) {
            val clipboard = getSystemService(context, ClipboardManager::class.java)
            val clip = ClipData.newPlainText("label", text)
            clipboard?.setPrimaryClip(clip)
        }
        fun clearFocus(editText: TextInputEditText){
            if (editText.hasFocus()){
                editText.clearFocus()
            }
        }

        fun showSnackBar(msg: String?, view: View){
            if (msg != null) {
                Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show()
            }
        }

    }
}