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
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
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
            Log.i("myTest", "list done")
            val encodeHtml = Base64.encodeToString(
                list.items[0].player.embedHtml.toByteArray(),
                Base64.NO_PADDING
            )
            binding.webView.settings.javaScriptEnabled = true
//            binding.webView.settings.javaScriptCanOpenWindowsAutomatically = true
//            binding.webView.loadData(encodeHtml,"text/html; charset=utf-8","base64")
            Log.i("myTest", list.items[0].player.embedHtml)
            val pattern = Regex("src=\"(.+?)\"")
            val matchResult = pattern.find(list.items[0].player.embedHtml)


            binding.webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    view?.loadUrl(request?.url.toString());
                    return true;
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    if (view != null) {
                        simulateKeyboardEvent2(view, 1)
                    }
                }
            }
            binding.webView.webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    Log.i("myTest", "console.log-->${consoleMessage?.message()}")
                    return true
                }
            }

            binding.webView.settings.userAgentString = "PC"
            binding.webView.settings.mediaPlaybackRequiresUserGesture = true
            val link :String
            if (matchResult != null) {
                link =  matchResult.groupValues[1]
                binding.webView.loadUrl(link)
            } else {
                Log.e("myTest","播放列表加载失败")
                Toast.makeText(this, "播放列表加载失败", Toast.LENGTH_SHORT).show()
            }

        }

        binding.button.setOnClickListener {
            simulateKeyboardEvent(binding.webView, 75)
        }

        binding.button2.setOnClickListener {
            simulateKeyboardEvent(binding.webView, 78, true)
        }

    }

    private fun simulateKeyboardEvent(view: WebView, keyCode: Int, withShift: Boolean = false) {
        val str =
            "javascript:console.log(document.querySelector('.html5-video-player').dispatchEvent(new KeyboardEvent('keydown',{keyCode:$keyCode,shiftKey:$withShift})))"
        view.evaluateJavascript(str, null)
    }

    private fun simulateKeyboardEvent2(view: WebView, keyCode: Int) {

        val event = MotionEvent.obtain(
            SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis(),
            MotionEvent.ACTION_DOWN,
            0.0f,
            0.0f,
            0
        )
        view.dispatchTouchEvent(event)
        event.recycle()
        val event2 = MotionEvent.obtain(
            SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent
                .ACTION_UP, 0.0f, 0.0f, 0
        )
        view.dispatchTouchEvent(event2)
        event2.recycle()
    }

    // 模拟按键事件
    private fun dispatchKeyEvent(webView: View, primaryCode: Int, keyCode: Int) {
        val currentTime = System.currentTimeMillis()
        val downEvent = KeyEvent(
            SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis(),
            KeyEvent.ACTION_DOWN,
            keyCode,
            1,
            0
        )

        webView.dispatchKeyEvent(downEvent)

        val downEvent2 = KeyEvent(
            SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis(),
            KeyEvent.ACTION_DOWN,
            primaryCode,
            1,
            0
        )

        webView.dispatchKeyEvent(downEvent2)

        val upEvent = KeyEvent(
            SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis(),
            KeyEvent.ACTION_UP,
            primaryCode,
            1,
            0
        )
        webView.dispatchKeyEvent(upEvent)

        val upEvent2 = KeyEvent(
            SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis(),
            KeyEvent.ACTION_UP,
            keyCode,
            1,
            0
        )
        webView.dispatchKeyEvent(upEvent2)
    }
}