package com.example.agoracall

import android.Manifest
import android.os.Bundle
import android.view.TextureView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import io.agora.rtc2.*
import io.agora.rtc2.video.VideoCanvas

class CallActivity : AppCompatActivity() {
    private val appId = "ВАШ_APP_ID" // Замените здесь!
    private val channelName = "test_channel"
    
    private var agoraEngine: RtcEngine? = null
    private lateinit var localView: TextureView
    private lateinit var remoteView: TextureView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        
        localView = findViewById(R.id.local_video_view)
        remoteView = findViewById(R.id.remote_video_view)
        
        requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), 1)
        
        initializeAgoraEngine()
        joinChannel()
    }

    private fun initializeAgoraEngine() {
        try {
            val config = RtcEngineConfig().apply {
                mContext = applicationContext
                mAppId = appId
                mEventHandler = object : IRtcEngineEventHandler() {
                    override fun onUserJoined(uid: Int, elapsed: Int) {
                        runOnUiThread { setupRemoteVideo(uid) }
                    }
                }
            }
            agoraEngine = RtcEngine.create(config)
            agoraEngine?.enableVideo()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupLocalVideo() {
        agoraEngine?.setupLocalVideo(VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, 0))
    }

    private fun setupRemoteVideo(uid: Int) {
        agoraEngine?.setupRemoteVideo(VideoCanvas(remoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid))
    }

    private fun joinChannel() {
        agoraEngine?.joinChannel(null, channelName, "", 0)
        setupLocalVideo()
    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine?.leaveChannel()
        RtcEngine.destroy()
    }
}
