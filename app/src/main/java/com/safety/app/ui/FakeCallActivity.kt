package com.safety.app.ui

import android.media.RingtoneManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.safety.app.R

class FakeCallActivity : AppCompatActivity() {

    private var ringtone: android.media.Ringtone? = null
    private val handler = Handler(Looper.getMainLooper())
    private var seconds = 0
    private var isConnected = false
    
    private lateinit var layoutButtons: android.view.ViewGroup
    private lateinit var btnEndCall: android.widget.ImageButton
    private lateinit var tvStatus: android.widget.TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fake_call)
        
        // immersive mode
        hideSystemUI()

        layoutButtons = findViewById(R.id.layoutButtons)
        btnEndCall = findViewById(R.id.btnEndCall)
        tvStatus = findViewById(R.id.tvCallStatus)
        tvStatus.text = "Incoming call..."

        // Play Ringtone
        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        ringtone = RingtoneManager.getRingtone(applicationContext, ringtoneUri)
        ringtone?.play()

        findViewById<ImageButton>(R.id.btnAccept).setOnClickListener {
            answerCall()
        }

        findViewById<ImageButton>(R.id.btnDecline).setOnClickListener {
            endCall()
        }
        
        btnEndCall.setOnClickListener {
            endCall()
        }

        // Auto-stop ringing if not answered
        handler.postDelayed({
            if (!isConnected && ringtone?.isPlaying == true) {
                endCall()
            }
        }, 45000)
    }
    
    private fun answerCall() {
        isConnected = true
        ringtone?.stop()
        
        layoutButtons.visibility = android.view.View.GONE
        btnEndCall.visibility = android.view.View.VISIBLE
        tvStatus.text = "00:00"
        
        startTimer()
    }
    
    private fun startTimer() {
        handler.post(object : Runnable {
            override fun run() {
                if (!isConnected) return
                
                val mins = seconds / 60
                val secs = seconds % 60
                val time = String.format("%02d:%02d", mins, secs)
                tvStatus.text = time
                
                seconds++
                handler.postDelayed(this, 1000)
            }
        })
    }
    
    private fun endCall() {
        isConnected = false
        ringtone?.stop()
        finish()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or android.view.View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        ringtone?.stop()
        handler.removeCallbacksAndMessages(null)
    }
}
