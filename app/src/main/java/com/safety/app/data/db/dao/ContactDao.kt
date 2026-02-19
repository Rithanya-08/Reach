package com.safety.app.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.safety.app.data.db.entities.EmergencyContact

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: EmergencyContact)

    @Delete
    suspend fun deleteContact(contact: EmergencyContact)

    @Query("SELECT * FROM emergency_contacts WHERE userId = :userId ORDER BY priority ASC")
    fun getContacts(userId: Long): LiveData<List<EmergencyContact>>

    @Query("SELECT * FROM emergency_contacts WHERE userId = :userId ORDER BY priority ASC")
    suspend fun getContactsSync(userId: Long): List<EmergencyContact>
    
    @Query("DELETE FROM emergency_contacts WHERE userId = :userId")
    suspend fun clearContacts(userId: Long)
}
