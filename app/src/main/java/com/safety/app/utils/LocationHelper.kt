package com.safety.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LocationHelper(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getLocationFlow(intervalMs: Long = 5000): Flow<Location> = callbackFlow {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(intervalMs)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(it) }
            }
        }

        fusedLocationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
            .addOnFailureListener { close(it) }

        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(onSuccess: (Location?) -> Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { onSuccess(it) }
    }
}
