package com.safety.app.ui

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.safety.app.R
import com.safety.app.logic.JourneyMonitoringService
import java.io.File

class AudioRecordingsActivity : AppCompatActivity() {

    private lateinit var adapter: RecordingsAdapter
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_recordings)

        // Stop Recording Button
        findViewById<Button>(R.id.btnStopRecording).setOnClickListener {
            val serviceIntent = Intent(this, JourneyMonitoringService::class.java).apply {
                action = "ACTION_STOP_RECORDING"
            }
            startService(serviceIntent)
            Toast.makeText(this, "Stopping Recording...", Toast.LENGTH_SHORT).show()
            // Refresh list after a delay
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                loadRecordings()
            }, 1000)
        }

        // RecyclerView
        val rvRecordings = findViewById<RecyclerView>(R.id.rvRecordings)
        rvRecordings.layoutManager = LinearLayoutManager(this)
        adapter = RecordingsAdapter { file ->
            playAudio(file)
        }
        rvRecordings.adapter = adapter

        loadRecordings()
    }

    private fun loadRecordings() {
        val cacheDir = externalCacheDir
        val files = cacheDir?.listFiles { _, name -> name.endsWith(".3gp") }
            ?.sortedByDescending { it.lastModified() }
            ?.toList() ?: emptyList()
        
        adapter.submitList(files)
        
        if (files.isEmpty()) {
            Toast.makeText(this, "No recordings found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playAudio(file: File) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@AudioRecordingsActivity, Uri.fromFile(file))
                prepare()
                start()
            }
            Toast.makeText(this, "Playing: ${file.name}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to play audio", Toast.LENGTH_SHORT).show()
        }
    }
    
    // Broadcast Receiver for Stopping Recording
    private val stopReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: android.content.Context, intent: Intent) {
            if (intent.action == "ACTION_RECORDING_SAVED") {
                val filePath = intent.getStringExtra("file_path")
                if (filePath != null) {
                    Toast.makeText(context, "Recording Saved!", Toast.LENGTH_SHORT).show()
                    loadRecordings() // Refresh list
                    shareFileToFirstContact(File(filePath))
                }
            }
        }
    }
    
    private fun shareFileToFirstContact(file: File) {
        // Fetch First Contact
        val db = com.safety.app.data.db.AppDatabase.getDatabase(this)
        val repository = com.safety.app.data.repository.SafetyRepository(db)
        
        repository.user.observe(this) { user ->
             if (user != null) {
                 repository.getContacts(user.userId).observe(this) { contacts ->
                     val firstContact = contacts.firstOrNull()
                     if (firstContact != null) {
                         shareFile(file, firstContact.phoneNumber)
                         // Remove observers to prevent multiple triggers if data changes? 
                         // Simplification: In a real app we'd use a one-shot fetch or coroutine
                     } else {
                         Toast.makeText(this, "No contact to share with!", Toast.LENGTH_SHORT).show()
                     }
                 }
             }
        }
    }

    private fun shareFile(file: File, phoneNumber: String) {
        try {
            val uri = androidx.core.content.FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "audio/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra("address", phoneNumber) // SMS/MMS hint
                putExtra(Intent.EXTRA_PHONE_NUMBER, phoneNumber) // Some apps use this
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Send Audio to $phoneNumber"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Sharing failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = android.content.IntentFilter("ACTION_RECORDING_SAVED")
        registerReceiver(stopReceiver, filter, android.os.Build.VERSION.SDK_INT.let { if (it >= 33) 2 else 0 } ) // RECEIVER_EXPORTED or 0? 0 is deprecated for unrelated, Context.RECEIVER_EXPORTED (0x2) for Android 13+
        // Basic register for now, addressing flags if needed
    }

    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(stopReceiver)
        } catch (e: Exception) {}
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}
