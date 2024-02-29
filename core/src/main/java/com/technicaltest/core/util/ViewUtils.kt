package com.blanccone.mimecloud.util

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView

object ViewUtils {
    /**
     * Use this when set View to Visible
     */
    fun View.show(): View {
        if(visibility != View.VISIBLE) {
            visibility = View.VISIBLE
        }
        return this
    }

    /**
     * Use this when set View to Gone
     */
    fun View.hide(): View {
        if(visibility != View.GONE) {
            visibility = View.GONE
        }
        return this
    }

    fun unknownMsg(): String = "Oops...Something went wrong"

    fun View.hideKeyboard(){
        val imm = this.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    }
}