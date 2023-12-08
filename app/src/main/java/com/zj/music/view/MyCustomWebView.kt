package com.zj.music.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.webkit.WebView

class MyCustomWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,defStyleAttr: Int = 0
) : WebView(context, attrs,defStyleAttr) {
    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(View.VISIBLE)
    }

}