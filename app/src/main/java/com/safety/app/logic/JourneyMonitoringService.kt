package com.safety.app.logic

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.safety.app.R
import com.safety.app.data.api.DirectionsApiService
import com.safety.app.data.db.AppDatabase
import com.safety.app.data.repository.SafetyRepository
import com.safety.app.ui.DashboardActivity
import com.safety.app.utils.LocationHelper
import kotlinx.coroutines.*

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.safety.app.data.db.entities.LocationLog
import com.safety.app.utils.NetworkUtils
import kotlin.math.sqrt

class JourneyMonitoringService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private lateinit var locationHelper: LocationHelper
    private lateinit var repository: SafetyRepository
    private val directionsApi = DirectionsApiService.create()
    
    // Volume Trigger
    private lateinit var volumeReceiver: android.content.BroadcastReceiver
    private var previousVolume: Int = 0
    private var volumeSequence = mutableListOf<String>() // Stores "UP", "DOWN"
    private var lastVolumeChangeTime: Long = 0
    
    // Constants
    private val PATTERN_TIMEOUT_MS = 3000L
    private val TRIGGER_PATTERN = listOf("UP", "DOWN", "UP")

    private var isMonitoring = false
    private var destinationLat: Double = 0.0
    private var destinationLng: Double = 0.0
    private var destinationName: String = ""
    private var journeyId: Long = 0
    
    private val API_KEY = "AIzaSyBWvTxKbh_ag2nmb_WqhyXJuMG0ImxLx6I"

    private var mediaRecorder: android.media.MediaRecorder? = null
    private var isRecording = false
    private var currentRecordingFile: String? = null

    companion object {
        const val CHANNEL_ID = "JourneyChannel"
        const val ACTION_STOP_MONITORING = "STOP_MONITORING"
        const val EXTRA_JOURNEY_ID = "extra_journey_id"
        const val EXTRA_DEST_LAT = "extra_dest_lat"
        const val EXTRA_DEST_LNG = "extra_dest_lng"
        const val EXTRA_DEST_NAME = "extra_dest_name"
    }

    private var wakeLock: android.os.PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
        locationHelper = LocationHelper(this)
        val db = AppDatabase.getDatabase(this)
        repository = SafetyRepository(db)
        createNotificationChannel()

        // Acquire WakeLock
        val powerManager = getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
        wakeLock = powerManager.newWakeLock(android.os.PowerManager.PARTIAL_WAKE_LOCK, "JourneyService::WakeLock")
        wakeLock?.acquire(10*60*1000L /*10 minutes*/) // Re-acquire in loop if needed, or indefinite

        // Initialize Volume Receiver
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
        previousVolume = audioManager.getStreamVolume(android.media.AudioManager.STREAM_MUSIC)
        
        volumeReceiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == "android.media.VOLUME_CHANGED_ACTION") {
                    handleVolumeChange(audioManager)
                }
            }
        }
        
        val filter = android.content.IntentFilter()
        filter.addAction("android.media.VOLUME_CHANGED_ACTION")
        registerReceiver(volumeReceiver, filter)
        
        android.widget.Toast.makeText(this, "Service Started: Use Vol Up-Down-Up to SOS", android.widget.Toast.LENGTH_LONG).show()
    }

    private fun handleVolumeChange(audioManager: android.media.AudioManager) {
        val currentVolume = audioManager.getStreamVolume(android.media.AudioManager.STREAM_MUSIC)
        val now = System.currentTimeMillis()
        
        // Reset sequence if too much time passed
        if (now - lastVolumeChangeTime > PATTERN_TIMEOUT_MS) {
            volumeSequence.clear()
        }
        
        if (currentVolume > previousVolume) {
            volumeSequence.add("UP")
            lastVolumeChangeTime = now
            android.widget.Toast.makeText(applicationContext, "Vol UP detected", android.widget.Toast.LENGTH_SHORT).show()
        } else if (currentVolume < previousVolume) {
            volumeSequence.add("DOWN")
            lastVolumeChangeTime = now
            android.widget.Toast.makeText(applicationContext, "Vol DOWN detected", android.widget.Toast.LENGTH_SHORT).show()
        } else {
             // Volume didn't change (e.g. at max/min limits), but event fired?
        }
        
        previousVolume = currentVolume
        
        // Check Pattern
        if (volumeSequence.size >= 3) {
            // Check last 3 entries
            val lastThree = volumeSequence.takeLast(3)
            android.widget.Toast.makeText(applicationContext, "Sequence: $lastThree", android.widget.Toast.LENGTH_SHORT).show()
            
            if (lastThree == TRIGGER_PATTERN) {
                volumeSequence.clear() // Reset prevents double trigger
                triggerSOS()
            }
        }
    }

    // ... (onStartCommand and startMonitoring remain same)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_MONITORING) {
            stopRecording()
            stopSelf()
            return START_NOT_STICKY
        }
        
        if (intent?.action == "START_RECORDING") {
            startRecording()
            return START_STICKY
        }

        // Check if starting in pure "Watch Mode" (no journey)
        if (intent?.action == "ACTION_START_WATCH_MODE") {
             val notification = createNotification("Safety Watch Active (No Journey)")
             startForeground(1, notification)
             // Just stay alive for Volume Trigger; do not start route monitoring loop
             return START_STICKY
        }
        
        if (intent?.action == "ACTION_STOP_RECORDING") {
            stopRecording()
            // Do not stop service, just stop recording
            return START_STICKY
        }

        // Normal Journey Mode
        journeyId = intent?.getLongExtra(EXTRA_JOURNEY_ID, 0) ?: 0
        destinationLat = intent?.getDoubleExtra(EXTRA_DEST_LAT, 0.0) ?: 0.0
        destinationLng = intent?.getDoubleExtra(EXTRA_DEST_LNG, 0.0) ?: 0.0
        destinationName = intent?.getStringExtra(EXTRA_DEST_NAME) ?: ""

        val notification = createNotification("Monitoring your journey...")
        startForeground(1, notification)

        startMonitoring()

        return START_STICKY
    }

    private fun startMonitoring() {
        if (isMonitoring) return
        isMonitoring = true

        serviceScope.launch {
            // TEST MODE: Force Alert Sequence
            delay(10 * 1000L) // Wait 10s
            if (isMonitoring) {
                 triggerSoftAlert("TEST: SIMULATED DELAY")
            }
            
            while (isMonitoring) {
                 locationHelper.getLastLocation { location ->
                     location?.let { loc ->
                         serviceScope.launch {
                             // 0. Battery Check
                             val batteryManager = getSystemService(Context.BATTERY_SERVICE) as android.os.BatteryManager
                             val batteryLevel = batteryManager.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY)
                             if (batteryLevel <= 15) {
                                  updateNotification("Low Battery ($batteryLevel%)! Please charge.")
                             }

                             // 1. Offline Logging
                             val isOnline = NetworkUtils.isInternetAvailable(applicationContext)
                             val log = LocationLog(
                                 journeyId = journeyId,
                                 latitude = loc.latitude,
                                 longitude = loc.longitude,
                                 synced = isOnline
                             )
                             repository.insertLocationLog(log)
                             
                             if (isOnline) {
                                 // 2. Calculate ETA & Traffic
                                 checkTrafficAndEta(loc.latitude, loc.longitude)
                             } else {
                                 updateNotification("Offline Mode: Logging location locally")
                             }
                         }
                     }
                 }
                delay(2 * 60 * 1000) // Check every 2 minutes
            }
        }
    }

    // Updated Thresholds for Testing
    private val TEST_ETA_BUFFER_MS = 10 * 1000L // 10 Seconds Buffer!
    private val SOFT_ALERT_TIMEOUT_MS = 20 * 1000L // 20 Seconds to respond

    private fun broadcastUpdate(status: String, eta: String) {
        val intent = Intent("ACTION_JOURNEY_UPDATE")
        intent.putExtra("status", status)
        intent.putExtra("eta", eta)
        sendBroadcast(intent)
    }

    private suspend fun checkTrafficAndEta(currentLat: Double, currentLng: Double) {
        try {
            val origin = "$currentLat,$currentLng"
            val dest = "$destinationLat,$destinationLng"
            
            val response = directionsApi.getDirections(origin, dest, API_KEY)
            
            if (response.status == "OK" && response.routes.isNotEmpty()) {
                val leg = response.routes[0].legs.firstOrNull()
                val durationInTraffic = leg?.duration_in_traffic?.value ?: leg?.duration?.value ?: 0
                val etaText = leg?.duration_in_traffic?.text ?: "Unknown"
                
                val expectedArrivalTime = System.currentTimeMillis() + (durationInTraffic * 1000)
                
                // Broadcast Update UI
                broadcastUpdate("Monitoring Active", etaText)
                updateNotification("ETA: $etaText")

                // Smart Alert Logic
                // If current time > expected + buffer
                if (System.currentTimeMillis() > expectedArrivalTime + TEST_ETA_BUFFER_MS) {
                    triggerSoftAlert("Delayed by >1 min")
                }
                
            }
        } catch (e: Exception) {
            Log.e("Service", "Error checking traffic", e)
        }
    }

    // Shake Detection - Restored Methods
    private fun triggerSOS() {
        // 1. Start Audio Recording
        startRecording()

        // 2. Send SMS Directly (Background Safe)
        sendSOSDirectly()

        // 3. Trigger Fake Call (Visual Distraction/Help) - Might be blocked in bg but try
        try {
            val fakeCallIntent = Intent(this, com.safety.app.ui.FakeCallActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(fakeCallIntent)
        } catch (e: Exception) {
            Log.e("JourneyService", "Could not start FakeCallActivity from background", e)
        }
        
        val notification = createNotification("SHAKE DETECTED! Recording Audio & SOS Sent!")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    private fun sendSOSDirectly() {
        serviceScope.launch {
            try {
                // Use callback based location fetch
                 locationHelper.getLastLocation { loc ->
                     val mapsLink = if (loc != null) "https://maps.google.com/?q=${loc.latitude},${loc.longitude}" else "Unknown Location"
                     val message = "EMERGENCY! I need help. My location: $mapsLink"
                     
                     serviceScope.launch(Dispatchers.IO) {
                         val user = repository.getUserSync()
                         if (user != null) {
                             val userContacts = repository.getContactsSync(user.userId)
                             val firstContact = userContacts.firstOrNull()
                             
                             if (firstContact != null) {
                                 try {
                                     @Suppress("DEPRECATION")
                                     val smsManager = android.telephony.SmsManager.getDefault()
                                     smsManager.sendTextMessage(firstContact.phoneNumber, null, message, null, null)
                                     withContext(Dispatchers.Main) {
                                         android.widget.Toast.makeText(applicationContext, "SOS Sent to ${firstContact.name}", android.widget.Toast.LENGTH_SHORT).show()
                                     }
                                 } catch (e: Exception) {
                                     Log.e("JourneyService", "SMS Failed", e)
                                 }
                             }
                         }
                     }
                 }
            } catch (e: Exception) {
                Log.e("JourneyService", "Error in sendSOSDirectly", e)
            }
        }
    }

    private fun startRecording() {
        if (isRecording) return
        
        try {
            val fileName = "${externalCacheDir?.absolutePath}/sos_audio_${System.currentTimeMillis()}.3gp"
            
            mediaRecorder = android.media.MediaRecorder().apply {
                setAudioSource(android.media.MediaRecorder.AudioSource.MIC)
                setOutputFormat(android.media.MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(fileName)
                setAudioEncoder(android.media.MediaRecorder.AudioEncoder.AMR_NB)
                
                try {
                    prepare()
                    start()
                    isRecording = true
                    currentRecordingFile = fileName
                    Log.d("JourneyService", "Recording started: $fileName")
                } catch (e: Exception) {
                    Log.e("JourneyService", "MediaRecorder prepare() failed", e)
                }
            }
        } catch (e: Exception) {
             Log.e("JourneyService", "MediaRecorder init failed", e)
        }
    }

    private fun stopRecording() {
        if (isRecording) {
            try {
                mediaRecorder?.stop()
                mediaRecorder?.release()
                mediaRecorder = null
                isRecording = false
                Log.d("JourneyService", "Recording stopped")
                
                // Broadcast the file path
                currentRecordingFile?.let { path ->
                    val intent = Intent("ACTION_RECORDING_SAVED")
                    intent.putExtra("file_path", path)
                    sendBroadcast(intent)
                    currentRecordingFile = null
                }
                
            } catch (e: Exception) {
                Log.e("JourneyService", "Error stopping recorder", e)
            }
        }
    }

    private fun triggerSoftAlert(reason: String) {
        // Prevent multiple triggers
        if (!isMonitoring) return 

        // 1. Send SMS to Contact: "User is delayed..."
        sendSoftAlertSMS()

        // 2. Local Notification / Broadcast to Activity to show dialog
        val intent = Intent(this, DashboardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("ALERT", true)
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Are you safe?")
            .setContentText("Status: $reason. Verify safety inside app.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(2, notification)
        
        broadcastUpdate("ALERT: $reason", "CHECK SAFETY!")
        
        // 3. Start Escalation Timer (30s)
        serviceScope.launch {
            delay(SOFT_ALERT_TIMEOUT_MS)
            // If still monitoring (means user didn't cancel/confirm safety), trigger SOS
            if (isMonitoring) {
                 withContext(Dispatchers.Main) {
                     android.widget.Toast.makeText(applicationContext, "No response! Escalating to SOS!", android.widget.Toast.LENGTH_LONG).show()
                 }
                 triggerSOS()
            }
        }
    }
    
    private fun sendSoftAlertSMS() {
         serviceScope.launch(Dispatchers.IO) {
             val user = repository.getUserSync()
             if (user != null) {
                 val userContacts = repository.getContactsSync(user.userId)
                 val firstContact = userContacts.firstOrNull()
                 
                 if (firstContact != null) {
                     try {
                         val message = "Safety App Alert: ${user.fullName} is delayed or off-route. Checking their status now..."
                         @Suppress("DEPRECATION")
                         val smsManager = android.telephony.SmsManager.getDefault()
                         smsManager.sendTextMessage(firstContact.phoneNumber, null, message, null, null)
                     } catch (e: Exception) {
                         Log.e("JourneyService", "Soft Alert SMS Failed", e)
                     }
                 }
             }
         }
    }
    
    // updateNotification helper remains same...

    private fun createNotification(content: String): Notification {
        val intent = Intent(this, DashboardActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Safety Companion Active")
            .setContentText(content)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
    
    private fun updateNotification(content: String) {
        val notification = createNotification("Monitoring: $content")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Journey Monitoring Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRecording()
        isMonitoring = false
        serviceScope.cancel()
        try {
            unregisterReceiver(volumeReceiver)
        } catch (e: Exception) {
            Log.e("JourneyService", "Receiver not registered", e)
        }
        
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        } catch (e: Exception) {
            Log.e("JourneyService", "Error releasing wakelock", e)
        }
    }
}
