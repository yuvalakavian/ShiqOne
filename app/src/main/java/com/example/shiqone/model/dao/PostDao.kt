package com.example.shiqone.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shiqone.model.Post

@Dao
interface PostDao {

    @Query("SELECT * FROM Post")
    fun getAllPost(): LiveData<List<Post>>

    @Query("SELECT * FROM Post WHERE userID =:userID")
    fun getPostByUserId(userID: String): Post

    @Query("SELECT * FROM Post WHERE id =:id")
    fun getPostById(id: Int): Post

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts: Post)

    @Query("DELETE FROM Post")
    fun deleteAll()

    @Delete
    fun delete(post: Post)
}