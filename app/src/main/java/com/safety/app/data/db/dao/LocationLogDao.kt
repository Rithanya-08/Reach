package com.safety.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.safety.app.data.db.entities.LocationLog

@Dao
interface LocationLogDao {
    @Insert
    suspend fun insertLog(log: LocationLog)

    @Query("SELECT * FROM location_logs WHERE synced = 0")
    suspend fun getUnsyncedLogs(): List<LocationLog>

    @Query("UPDATE location_logs SET synced = 1 WHERE logId IN (:ids)")
    suspend fun markAsSynced(ids: List<Long>)
}
