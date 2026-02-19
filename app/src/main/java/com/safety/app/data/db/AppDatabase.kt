package com.safety.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.safety.app.data.db.dao.ContactDao
import com.safety.app.data.db.dao.JourneyDao
import com.safety.app.data.db.dao.LocationLogDao
import com.safety.app.data.db.dao.UserDao
import com.safety.app.data.db.entities.EmergencyContact
import com.safety.app.data.db.entities.Journey
import com.safety.app.data.db.entities.LocationLog
import com.safety.app.data.db.entities.User

@Database(
    entities = [User::class, EmergencyContact::class, Journey::class, LocationLog::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun contactDao(): ContactDao
    abstract fun journeyDao(): JourneyDao
    abstract fun locationLogDao(): LocationLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "safety_app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
