package com.example.shiqone.model.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shiqone.base.MyApplication
import com.example.shiqone.model.Post
import com.example.shiqone.model.User

@Database(entities = [Post::class, User::class], version = 8)
abstract class AppLocalDbRepository: RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao
}
object AppLocalDb {

    val database: AppLocalDbRepository by lazy {

        val context = MyApplication.Globals.context
            ?: throw IllegalStateException("Application context is missing")

        Room.databaseBuilder(
            context = context,
            klass = AppLocalDbRepository::class.java,
            name = "dbFileName.db"
        ).fallbackToDestructiveMigration().build()
    }
}