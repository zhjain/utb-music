package com.zj.music

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.zj.music.databinding.ActivityPlaylistBinding
import com.zj.music.dto.Playlist


class PlaylistActivity : ComponentActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val list = intent.getSerializableExtra("playlist", Playlist::class.java)
        if (list != null) {
            Log.i("myTest","list done")
            val encodeHtml = Base64.encodeToString(list.items[0].player.embedHtml.toByteArray(),Base64.NO_PADDING)
            binding.webView.settings.javaScriptEnabled=true
//            binding.webView.loadData(encodeHtml,"text/html; charset=utf-8","base64")

            val link = "http://www.youtube.com/embed/videoseries?list=PLRTW6h03whLlKyanwalcNMkf-FSXWfldn"
            binding.webView.webViewClient = object : WebViewClient() {

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    view?.loadUrl(request?.url.toString());
                    return true;
                }
            }
            binding.webView.loadUrl(link)


        }
        binding.button.setOnClickListener {
//            dispatchKeyEvent(binding.webView,KeyEvent.KEYCODE_N, KeyEvent.KEYCODE_SHIFT_LEFT);
            simulateKeyboardEvent(binding.webView,KeyEvent.KEYCODE_SPACE)
//            simulateKeyboardEvent1(binding.webView,1)
        }

        binding.button2.setOnClickListener {
            dispatchKeyEvent(binding.webView,KeyEvent.KEYCODE_N, KeyEvent.KEYCODE_SHIFT_LEFT);
        }

    }

    private fun simulateKeyboardEvent(view: View, keyCode: Int){

        val event = KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), KeyEvent.ACTION_DOWN, keyCode, 1, 0)
        view.dispatchKeyEvent(event)
        val event2 = KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), KeyEvent.ACTION_UP, keyCode, 1, 0)
        view.dispatchKeyEvent(event2)

//        val event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0.0f, 0.0f, 0)
//        view.dispatchTouchEvent(event)
//        event.recycle()
//        val event2 = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent
//            .ACTION_UP, 0.0f, 0.0f, 0)
//        view.dispatchTouchEvent(event2)
//        event2.recycle()
    }
    private fun simulateKeyboardEvent2(view: View, keyCode: Int){

        val event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0.0f, 0.0f, 0)
        view.dispatchTouchEvent(event)
        event.recycle()
        val event2 = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent
            .ACTION_UP, 0.0f, 0.0f, 0)
        view.dispatchTouchEvent(event2)
        event2.recycle()
    }

    // 模拟按键事件
    private fun dispatchKeyEvent(webView:View,primaryCode: Int, keyCode: Int) {
        val currentTime = System.currentTimeMillis()
        val downEvent = KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), KeyEvent.ACTION_DOWN, keyCode, 1, 0)

        webView.dispatchKeyEvent(downEvent)

        val downEvent2 = KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), KeyEvent.ACTION_DOWN, primaryCode, 1, 0)

        webView.dispatchKeyEvent(downEvent2)

        val upEvent = KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), KeyEvent.ACTION_UP, primaryCode, 1, 0)
        webView.dispatchKeyEvent(upEvent)

        val upEvent2 = KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), KeyEvent.ACTION_UP, keyCode, 1, 0)
        webView.dispatchKeyEvent(upEvent2)
    }
}