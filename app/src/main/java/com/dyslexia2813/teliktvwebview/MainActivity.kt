package com.dyslexia2813.teliktvwebview

import android.app.Activity
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.DecoderReuseEvaluation
import androidx.media3.exoplayer.analytics.AnalyticsListener
import java.util.Locale

class MainActivity : Activity() {
    private lateinit var player: ExoPlayer
    private lateinit var status: TextView
    private var decoderName = "unknown"
    private var videoFormat = "unknown"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemUi()

        val root = FrameLayout(this).apply { setBackgroundColor(Color.BLACK) }
        val surface = SurfaceView(this)
        root.addView(surface, FrameLayout.LayoutParams(-1, -1))

        status = TextView(this).apply {
            setTextColor(Color.WHITE)
            setBackgroundColor(0xCC000000.toInt())
            textSize = 16f
            setPadding(18, 18, 18, 18)
            text = "NATIVE PLAYER TEST\nStarting..."
        }
        val statusLp = FrameLayout.LayoutParams(-2, -2)
        statusLp.leftMargin = 20
        statusLp.topMargin = 20
        root.addView(status, statusLp)
        setContentView(root)

        updateStatus("Activity started")

        try {
            val mediaUri = Uri.parse("android.resource://$packageName/${R.raw.test}")
            player = ExoPlayer.Builder(this).build()
            player.setVideoSurfaceView(surface)

            player.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    updateStatus("state=" + when (state) {
                        Player.STATE_IDLE -> "IDLE"
                        Player.STATE_BUFFERING -> "BUFFERING"
                        Player.STATE_READY -> "READY"
                        Player.STATE_ENDED -> "ENDED"
                        else -> "UNKNOWN"
                    })
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    updateStatus("isPlaying=$isPlaying")
                }

                override fun onPlayerError(error: PlaybackException) {
                    updateStatus("PLAYER ERROR\n${error.errorCodeName}\n${error.message}")
                }
            })

            player.addAnalyticsListener(object : AnalyticsListener {
                override fun onVideoDecoderInitialized(
                    eventTime: AnalyticsListener.EventTime,
                    decoderName: String,
                    initializedTimestampMs: Long,
                    initializationDurationMs: Long
                ) {
                    this@MainActivity.decoderName = decoderName
                    updateStatus("decoder initialized")
                }

                override fun onVideoInputFormatChanged(
                    eventTime: AnalyticsListener.EventTime,
                    format: Format,
                    decoderReuseEvaluation: DecoderReuseEvaluation
                ) {
                    videoFormat = String.format(
                        Locale.US,
                        "%dx%d %s",
                        format.width,
                        format.height,
                        format.sampleMimeType ?: "unknown"
                    )
                    updateStatus("video format changed")
                }
            })

            updateStatus("ExoPlayer created")
            player.setMediaItem(MediaItem.fromUri(mediaUri))
            updateStatus("media item set")
            player.prepare()
            updateStatus("prepare() called")
            player.playWhenReady = true
        } catch (t: Throwable) {
            updateStatus("FATAL EXCEPTION\n${t.javaClass.name}\n${t.message}")
            throw t
        }
    }

    private fun updateStatus(event: String) {
        if (!::status.isInitialized) return
        runOnUiThread {
            val playerState = if (::player.isInitialized) {
                "\nstate=${player.playbackState} isPlaying=${player.isPlaying}" +
                    "\ndecoder=$decoderName" +
                    "\nvideo=$videoFormat" +
                    "\nposition=${String.format(Locale.US, "%.2f", player.currentPosition / 1000.0)} s"
            } else ""
            status.text = "NATIVE PLAYER TEST\nevent: $event$playerState"
        }
    }

    private fun hideSystemUi() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUi()
    }

    override fun onDestroy() {
        if (::player.isInitialized) player.release()
        super.onDestroy()
    }
}
