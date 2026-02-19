package com.safety.app.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "location_logs",
    foreignKeys = [ForeignKey(
        entity = Journey::class,
        parentColumns = ["journeyId"],
        childColumns = ["journeyId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class LocationLog(
    @PrimaryKey(autoGenerate = true) val logId: Long = 0,
    val journeyId: Long,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val synced: Boolean = false
)
