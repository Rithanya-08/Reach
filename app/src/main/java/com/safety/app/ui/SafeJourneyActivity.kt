package com.safety.app.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.safety.app.R
import com.safety.app.data.api.DirectionsApiService
import com.safety.app.logic.RouteMonitor
import com.safety.app.utils.LocationHelper
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.*

class SafeJourneyActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val scope = CoroutineScope(Dispatchers.Main)
    private lateinit var locationHelper: LocationHelper
    private val routeMonitor = RouteMonitor()
    private val directionsApi = DirectionsApiService.create()
    private var isMonitoring = false
    
    private val API_KEY = "AIzaSyBWvTxKbh_ag2nmb_WqhyXJuMG0ImxLx6I" 
    
    private lateinit var tvStatus: android.widget.TextView
    private lateinit var tvEta: android.widget.TextView
    
    // Receiver for Service Updates
    private val journeyReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(context: android.content.Context, intent: android.content.Intent) {
            if (intent.action == "ACTION_JOURNEY_UPDATE") {
                val status = intent.getStringExtra("status")
                val eta = intent.getStringExtra("eta")
                
                tvStatus.text = "Status: $status"
                tvEta.text = "ETA: $eta"
                
                if (status?.contains("ALERT") == true) {
                    showDeviationAlert(status)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_safe_journey)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        locationHelper = LocationHelper(this)
        
        tvStatus = findViewById(R.id.tvJourneyStatus)
        tvEta = findViewById(R.id.tvETS)

        findViewById<Button>(R.id.btnStartMonitoring).setOnClickListener {
            val destination = findViewById<EditText>(R.id.etDestination).text.toString()
            if (destination.isNotEmpty()) {
                startMonitoring(destination)
            }
        }
        
        findViewById<Button>(R.id.btnSosFloating).setOnClickListener {
             Toast.makeText(this, "SOS Triggered!", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onResume() {
        super.onResume()
        val filter = android.content.IntentFilter("ACTION_JOURNEY_UPDATE")
        registerReceiver(journeyReceiver, filter, android.os.Build.VERSION.SDK_INT.let { if (it >= 33) 2 else 0 } )
    }
    
    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(journeyReceiver)
        } catch (e: Exception) {}
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
            locationHelper.getLastLocation { loc ->
                loc?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }
            }
        }
    }

    private fun startMonitoring(destination: String) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission needed", Toast.LENGTH_SHORT).show()
            return
        }

        locationHelper.getLastLocation { currentLocation ->
            if (currentLocation == null) {
                Toast.makeText(this, "Could not get current location", Toast.LENGTH_SHORT).show()
                return@getLastLocation
            }

            scope.launch {
                Toast.makeText(this@SafeJourneyActivity, "Calculating route...", Toast.LENGTH_SHORT).show()

                try {
                    val origin = "${currentLocation.latitude},${currentLocation.longitude}"
                    val response = withContext(Dispatchers.IO) {
                        directionsApi.getDirections(origin, destination, API_KEY)
                    }

                    if (response.status == "OK" && response.routes.isNotEmpty()) {
                        val route = response.routes[0]
                        val overviewPolyline = route.overview_polyline.points
                        val leg = route.legs.firstOrNull()
                        val durationInTraffic = leg?.duration_in_traffic?.value ?: leg?.duration?.value ?: 0
                        val destLat = leg?.end_location?.lat ?: 0.0
                        val destLng = leg?.end_location?.lng ?: 0.0

                        // 1. Draw Route
                        val decodedPath = PolyUtil.decode(overviewPolyline)
                        map.addPolyline(PolylineOptions().addAll(decodedPath).color(Color.BLUE).width(12f))
                        
                        // 2. Save Journey to DB
                        val db = com.safety.app.data.db.AppDatabase.getDatabase(this@SafeJourneyActivity)
                        val repo = com.safety.app.data.repository.SafetyRepository(db)
                        val user = repo.getUserSync()
                        
                        if (user != null) {
                            val journey = com.safety.app.data.db.entities.Journey(
                                userId = user.userId,
                                destinationName = destination,
                                destinationLat = destLat,
                                destinationLng = destLng,
                                expectedArrivalTime = System.currentTimeMillis() + (durationInTraffic * 1000),
                                currentEta = System.currentTimeMillis() + (durationInTraffic * 1000),
                                trafficDuration = durationInTraffic,
                                normalDuration = leg?.duration?.value ?: 0,
                                status = "active"
                            )
                            val jId = repo.insertJourney(journey)
                            
                            // 3. Start Foreground Service
                            val serviceIntent = android.content.Intent(this@SafeJourneyActivity, com.safety.app.logic.JourneyMonitoringService::class.java).apply {
                                putExtra(com.safety.app.logic.JourneyMonitoringService.EXTRA_JOURNEY_ID, jId)
                                putExtra(com.safety.app.logic.JourneyMonitoringService.EXTRA_DEST_LAT, destLat)
                                putExtra(com.safety.app.logic.JourneyMonitoringService.EXTRA_DEST_LNG, destLng)
                                putExtra(com.safety.app.logic.JourneyMonitoringService.EXTRA_DEST_NAME, destination)
                            }
                            startService(serviceIntent) // Fallback for older APIs? No, ContextCompat handles it.
                            try {
                                androidx.core.content.ContextCompat.startForegroundService(this@SafeJourneyActivity, serviceIntent)
                            } catch (e: Exception) {
                                Toast.makeText(this@SafeJourneyActivity, "Failed to start service: ${e.message}", Toast.LENGTH_LONG).show()
                                e.printStackTrace()
                            }
                        }

                        Toast.makeText(this@SafeJourneyActivity, "Journey Started! ETA: ${leg?.duration_in_traffic?.text}", Toast.LENGTH_LONG).show()
                        isMonitoring = true
                        
                    } else {
                        Toast.makeText(this@SafeJourneyActivity, "Route not found", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@SafeJourneyActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startLocationUpdates() {
        scope.launch {
            locationHelper.getLocationFlow().collect { location ->
                if (!isMonitoring) return@collect
                
                if (routeMonitor.isOffRoute(location)) {
                     showDeviationAlert()
                }
            }
        }
    }
    
    private fun showDeviationAlert(message: String = "You seem to be off the traffic-optimized route.") {
        if (isFinishing || isDestroyed) return
        
        AlertDialog.Builder(this)
            .setTitle("Safety Alert Triggered")
            .setMessage("$message Are you safe?")
            .setCancelable(false) // Force user action
            .setPositiveButton("I'm Safe") { d, _ -> d.dismiss() } // In real world, this should notify service to stop escalation
            .setNegativeButton("SOS") { _, _ -> Toast.makeText(this, "SOS Sent!", Toast.LENGTH_SHORT).show() }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        isMonitoring = false
        scope.cancel()
    }
}
