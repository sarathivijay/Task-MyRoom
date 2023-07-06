package com.tws.taskroom.feature_room.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tws.taskroom.feature_room.domain.model.MyRoom

@Database(entities = [MyRoom::class], version = 1)
abstract class MyRoomDatabase : RoomDatabase() {

    abstract val roomDao: RoomDao

    companion object {
        const val DATABASE_NAME = "myRoom_db"
    }

}