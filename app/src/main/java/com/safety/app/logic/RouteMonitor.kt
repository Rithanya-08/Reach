package com.safety.app.logic

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RouteMonitor {

    private var routePolyline: List<LatLng> = emptyList()
    private val deviationThresholdMeters = 200.0 // 200 meters allowed deviation
    private var estimatedDurationSeconds: Long = 0

    fun setRoute(polylineEncoded: String, durationSeconds: Long) {
        routePolyline = PolyUtil.decode(polylineEncoded)
        estimatedDurationSeconds = durationSeconds
    }

    fun isOffRoute(currentLocation: Location): Boolean {
        if (routePolyline.isEmpty()) return false
        
        val currentLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val isOnPath = PolyUtil.isLocationOnPath(currentLatLng, routePolyline, true, deviationThresholdMeters)
        
        return !isOnPath
    }

    // Check if traffic is causing significant delays compared to initial plan
    // In a real implementation, we would re-fetch the route from API to get updated traffic info.
    // Here we analyze if we are moving slower than expected (simplified for local logic).
    fun isTrafficDelaySignificant(elapsedTimeSeconds: Long, coveredDistanceMeters: Double): Boolean {
        // Simple logic: if speed is very low (< 5 km/h) for a significant time (e.g., 5 mins)
        // AND we covered some distance (to filter out stops).
        // For this demo, let's just return true if time > 0 and distance is suspiciously low for that time.
        if (elapsedTimeSeconds > 300 && coveredDistanceMeters < 100) {
            return true
        }
        return false 
    }
}
