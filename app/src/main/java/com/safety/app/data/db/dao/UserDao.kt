package com.safety.app.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.safety.app.data.db.entities.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users LIMIT 1")
    fun getUser(): LiveData<User>

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getUserSync(): User?

    @Query("DELETE FROM users")
    suspend fun clearUser()
}
