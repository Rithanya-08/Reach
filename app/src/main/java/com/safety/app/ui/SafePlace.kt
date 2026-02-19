package com.safety.app.ui

data class SafePlace(
    val name: String, 
    val address: String, 
    val phoneNumber: String?, 
    val lat: Double, 
    val lng: Double
)
