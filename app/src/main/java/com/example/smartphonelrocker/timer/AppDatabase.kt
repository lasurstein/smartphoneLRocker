package com.example.smartphonelrocker.timer

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [MyTimer::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun timerDao(): TimerDao

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.timerDao())
                }
            }
        }

        suspend fun populateDatabase(timerDao: TimerDao) {
            // Delete all content here.
            timerDao.deleteAll()

            // Add sample words.
            var timer = MyTimer(id = 0, name = "Wake up", hour = 7, min = 0, time = "07:00")
            timerDao.insertTimer(timer)
            timer = MyTimer(id = 1, name = "Report", hour = 15, min = 30, time = "15:30")
            timerDao.insertTimer(timer)

        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "timers"
                ).build()
                INSTANCE = instance

                instance
            }
        }
    }
}