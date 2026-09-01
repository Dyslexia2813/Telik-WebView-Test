package com.dyslexia2813.teliktvwebview

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout

class MainActivity : Activity() {

    companion object {
        private const val USER_AGENT = "Mozilla/5.0 (Linux; Android 10; Android TV) AppleWebKit/537.36 Chrome/120 Safari/537.36"
        private const val MP4_URL = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4"
    }

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemUi()

        webView = WebView(this).apply {
            setBackgroundColor(Color.BLACK)
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                mediaPlaybackRequiresUserGesture = false
                loadsImagesAutomatically = true
                blockNetworkImage = false
                cacheMode = WebSettings.LOAD_DEFAULT
                userAgentString = USER_AGENT
            }
            webViewClient = WebViewClient()
        }

        setContentView(FrameLayout(this).apply {
            setBackgroundColor(Color.BLACK)
            addView(webView, FrameLayout.LayoutParams(-1, -1))
        })

        val html = """
            <!doctype html>
            <html>
            <head>
                <meta name=\"viewport\" content=\"width=device-width,initial-scale=1,maximum-scale=1\">
                <style>
                    html, body { margin:0; padding:0; width:100%; height:100%; background:#000; overflow:hidden; }
                    video { width:100%; height:100%; object-fit:contain; background:#000; }
                </style>
            </head>
            <body>
                <video id=\"video\" autoplay muted playsinline preload=\"auto\">
                    <source src=\"$MP4_URL\" type=\"video/mp4\">
                </video>
                <script>
                    const v = document.getElementById('video');
                    v.play().catch(() => {});
                </script>
            </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL(
            "https://interactive-examples.mdn.mozilla.net/",
            html,
            "text/html",
            "UTF-8",
            null
        )
    }

    private fun hideSystemUi() {
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            )
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUi()
    }

    override fun onDestroy() {
        webView.stopLoading()
        webView.destroy()
        super.onDestroy()
    }
}
