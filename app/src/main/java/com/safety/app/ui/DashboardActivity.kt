package com.safety.app.ui

import android.content.Intent
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.Places
import com.safety.app.R
import com.safety.app.data.db.AppDatabase
import com.safety.app.data.repository.SafetyRepository
import com.safety.app.utils.LocationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardActivity : AppCompatActivity() {

    private lateinit var locationHelper: LocationHelper
    private lateinit var repository: SafetyRepository
    private lateinit var contactsAdapter: ContactsAdapter
    private var currentUserId: Long = 0
    private val API_KEY = "AIzaSyBWvTxKbh_ag2nmb_WqhyXJuMG0ImxLx6I"

    private val permissionLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val smsGranted = permissions[android.Manifest.permission.SEND_SMS] ?: false
        
        if (locationGranted && smsGranted) {
            Toast.makeText(this, "Permissions Granted! You are safe.", Toast.LENGTH_SHORT).show()
            startWatchModeIfPossible()
        } else {
            Toast.makeText(this, "Permissions are required for Safety Features!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        checkAndRequestPermissions()
        startWatchModeIfPossible()

        // Database Init
        val db = AppDatabase.getDatabase(this)
        repository = SafetyRepository(db)
        
        // Setup RecyclerView
        val rvContacts = findViewById<RecyclerView>(R.id.rvDashboardContacts)
        rvContacts.layoutManager = LinearLayoutManager(this)
        contactsAdapter = ContactsAdapter(emptyList(), isReadOnly = true)
        rvContacts.adapter = contactsAdapter

        // Check User & Load Contacts
        repository.user.observe(this) { user ->
            if (user == null) {
                startActivity(Intent(this, RegistrationActivity::class.java))
                finish()
            } else {
                currentUserId = user.userId
                loadContacts()
            }
        }

        initializePlaces()

        locationHelper = LocationHelper(this)

        setupButtons()
        
        handleIntent(intent)
    }

    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.CALL_PHONE,
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.RECORD_AUDIO
        )

        val allGranted = permissions.all {
            androidx.core.content.ContextCompat.checkSelfPermission(this, it) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }

        if (!allGranted) {
            permissionLauncher.launch(permissions)
        }
    }

    private fun initializePlaces() {
         try {
            if (!Places.isInitialized()) {
                Places.initialize(applicationContext, API_KEY)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == "TRIGGER_SOS") {
            sendSOS()
        }
        
        if (intent?.getBooleanExtra("ALERT", false) == true) {
            showSafetyCheckDialog()
        }
    }

    private fun showSafetyCheckDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Safety Check")
            .setMessage("We detected a delay in your journey. Are you safe?")
            .setPositiveButton("I'm Safe") { d, _ -> d.dismiss() }
            .setNegativeButton("SOS") { _, _ -> sendSOS() }
            .show()
    }

    private fun loadContacts() {
        repository.getContacts(currentUserId).observe(this) { contacts ->
            contactsAdapter.updateList(contacts)
        }
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnStartJourney).setOnClickListener {
            startActivity(Intent(this, SafeJourneyActivity::class.java))
        }

        findViewById<Button>(R.id.btnStartJourney).setOnClickListener {
            startActivity(Intent(this, SafeJourneyActivity::class.java))
        }

        // Auto-Start Watch Mode if permissions granted
        startWatchModeIfPossible()

        findViewById<Button>(R.id.btnFakeCall).setOnClickListener {
            startActivity(Intent(this, FakeCallActivity::class.java))
        }

        findViewById<Button>(R.id.btnManageContacts).setOnClickListener {
            startActivity(Intent(this, EmergencyContactsActivity::class.java))
        }

        findViewById<Button>(R.id.btnSafePlaces).setOnClickListener {
            startActivity(Intent(this, SafePlacesActivity::class.java))
        }

        findViewById<Button>(R.id.btnRecordings).setOnClickListener {
            startActivity(Intent(this, AudioRecordingsActivity::class.java))
        }

        findViewById<Button>(R.id.btnSOS).setOnClickListener {
            sendSOS()
        }
    }

    private fun sendSOS() {
         if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
             Toast.makeText(this, "SMS Permission Required for SOS!", Toast.LENGTH_LONG).show()
             permissionLauncher.launch(arrayOf(android.Manifest.permission.SEND_SMS))
             return
         }
         
         // Trigger Audio Recording in Service
         val serviceIntent = Intent(this, com.safety.app.logic.JourneyMonitoringService::class.java).apply {
             action = "START_RECORDING"
         }
         try {
             androidx.core.content.ContextCompat.startForegroundService(this, serviceIntent)
         } catch (e: Exception) {
             e.printStackTrace()
         }
         
        locationHelper.getLastLocation { location ->
            CoroutineScope(Dispatchers.IO).launch {
                val contacts = repository.getContactsSync(currentUserId)
                
                withContext(Dispatchers.Main) {
                    if (contacts.isEmpty()) {
                        Toast.makeText(this@DashboardActivity, "No emergency contacts found!", Toast.LENGTH_SHORT).show()
                        return@withContext
                    }

                    val mapsLink = if (location != null) 
                        "https://maps.google.com/?q=${location.latitude},${location.longitude}" 
                    else "Unknown Location"
                    
                    val message = "EMERGENCY! I need help. My location: $mapsLink"

                    @Suppress("DEPRECATION")
                    val smsManager = SmsManager.getDefault()
                    
                    // Send only to the FIRST contact as requested
                    val firstContact = contacts.firstOrNull()
                    if (firstContact != null) {
                        try {
                            smsManager.sendTextMessage(firstContact.phoneNumber, null, message, null, null)
                            Toast.makeText(this@DashboardActivity, "SOS Sent to ${firstContact.name}", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(this@DashboardActivity, "Failed to send to ${firstContact.name}", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
        }
    }

    private fun startWatchModeIfPossible() {
        if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
             val serviceIntent = Intent(this, com.safety.app.logic.JourneyMonitoringService::class.java).apply {
                 action = "ACTION_START_WATCH_MODE"
             }
             try {
                 androidx.core.content.ContextCompat.startForegroundService(this, serviceIntent)
             } catch (e: Exception) {
                 e.printStackTrace()
             }
        }
    }
}
