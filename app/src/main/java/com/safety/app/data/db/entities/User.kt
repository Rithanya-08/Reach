package com.safety.app.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Long = 0,
    val fullName: String,
    val age: Int,
    val gender: String,
    val phoneNumber: String,
    val address: String,
    val bloodGroup: String?,
    val medicalNotes: String?,
    val createdAt: Long = System.currentTimeMillis()
)
