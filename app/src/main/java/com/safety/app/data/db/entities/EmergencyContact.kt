package com.safety.app.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "emergency_contacts",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userId"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class EmergencyContact(
    @PrimaryKey(autoGenerate = true) val contactId: Long = 0,
    val userId: Long,
    val name: String,
    val phoneNumber: String,
    val priority: Int, // 1 = Primary, 2 = Secondary, etc.
    val createdAt: Long = System.currentTimeMillis()
)
