package com.safety.app.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.safety.app.data.db.entities.Journey

@Dao
interface JourneyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJourney(journey: Journey): Long

    @Update
    suspend fun updateJourney(journey: Journey)

    @Query("SELECT * FROM journeys WHERE journeyId = :id")
    fun getJourney(id: Long): LiveData<Journey>

    @Query("SELECT * FROM journeys WHERE status = 'active' ORDER BY startTime DESC LIMIT 1")
    suspend fun getActiveJourney(): Journey?
}
