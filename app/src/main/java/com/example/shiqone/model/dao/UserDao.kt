package com.example.shiqone.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shiqone.model.User

@Dao
interface UserDao {

    @Query("SELECT * FROM User")
    fun getAllUser(): LiveData<List<User>>

    @Query("SELECT * FROM User WHERE id =:id")
    fun getUserById(id: String): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts: User)

    @Query("DELETE FROM User")
    fun deleteAll()

    @Delete
    fun delete(post: User)
}