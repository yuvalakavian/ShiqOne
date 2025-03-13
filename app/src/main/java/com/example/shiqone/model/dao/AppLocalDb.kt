package com.example.shiqone.model.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shiqone.base.MyApplication
import com.example.shiqone.model.Post

@Database(entities = [Post::class], version = 6)
abstract class AppLocalDbRepository: RoomDatabase() {
    abstract fun postDao() : PostDao
}

object AppLocalDb {

    val database: AppLocalDbRepository by lazy {

        val context = MyApplication.Globals.context ?: throw IllegalStateException("Application context is missing")

        Room.databaseBuilder(
            context = context,
            klass = AppLocalDbRepository::class.java,
            name = "dbFileName.db"
        ).fallbackToDestructiveMigration().build()
    }
}