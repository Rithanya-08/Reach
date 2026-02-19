package com.safety.app.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "journeys",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userId"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Journey(
    @PrimaryKey(autoGenerate = true) val journeyId: Long = 0,
    val userId: Long,
    val destinationName: String,
    val destinationLat: Double,
    val destinationLng: Double,
    val startTime: Long = System.currentTimeMillis(),
    val expectedArrivalTime: Long,
    val currentEta: Long,
    val trafficDuration: Long,
    val normalDuration: Long,
    val status: String // active, completed, emergency
)
