package com.palavrizar.tec.palavrizapp.utils.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.palavrizar.tec.palavrizapp.models.VideoWatchedTime
import com.palavrizar.tec.palavrizapp.utils.dao.VideoWatchedTimeDao

@Database(entities = [VideoWatchedTime::class], version = 1)
abstract class PalavrizappRoomDatabase: RoomDatabase() {

    abstract fun videoWatchedDao(): VideoWatchedTimeDao

    companion object {
        private var INSTANCE: PalavrizappRoomDatabase? = null

        fun getInstance(context: Context): PalavrizappRoomDatabase? {
            createNewInstanceIfNecessary(context)
            return INSTANCE
        }

        private fun createNewInstanceIfNecessary(context: Context) {
            synchronized(this) {
                if (INSTANCE == null) {
                    val builder = Room.databaseBuilder(context, PalavrizappRoomDatabase::class.java, "palavrizapp.sqlite")
                            .fallbackToDestructiveMigration()
                    INSTANCE = builder.build()
                }
            }
        }
    }
}
