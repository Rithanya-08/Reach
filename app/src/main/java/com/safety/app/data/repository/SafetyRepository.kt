package com.safety.app.data.repository

import androidx.lifecycle.LiveData
import com.safety.app.data.db.AppDatabase
import com.safety.app.data.db.entities.EmergencyContact
import com.safety.app.data.db.entities.Journey
import com.safety.app.data.db.entities.LocationLog
import com.safety.app.data.db.entities.User

class SafetyRepository(private val db: AppDatabase) {

    // User
    val user: LiveData<User> = db.userDao().getUser()
    
    suspend fun insertUser(user: User) {
        db.userDao().insertUser(user)
    }

    suspend fun getUserSync(): User? {
        return db.userDao().getUserSync()
    }

    // Contacts
    fun getContacts(userId: Long): LiveData<List<EmergencyContact>> {
        return db.contactDao().getContacts(userId)
    }

    suspend fun getContactsSync(userId: Long): List<EmergencyContact> {
        return db.contactDao().getContactsSync(userId)
    }

    suspend fun insertContact(contact: EmergencyContact) {
        db.contactDao().insertContact(contact)
    }

    suspend fun deleteContact(contact: EmergencyContact) {
        db.contactDao().deleteContact(contact)
    }

    // Journey
    suspend fun insertJourney(journey: Journey): Long {
        return db.journeyDao().insertJourney(journey)
    }

    suspend fun getActiveJourney(): Journey? {
        return db.journeyDao().getActiveJourney()
    }
    
    // Logs
    suspend fun insertLocationLog(log: LocationLog) {
        db.locationLogDao().insertLog(log)
    }
}
