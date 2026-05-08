package com.example.executionapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Goal::class, Step::class, PendingRequest::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun goalDao(): GoalDao
    abstract fun stepDao(): StepDao
    abstract fun pendingRequestDao(): PendingRequestDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "execution_app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
