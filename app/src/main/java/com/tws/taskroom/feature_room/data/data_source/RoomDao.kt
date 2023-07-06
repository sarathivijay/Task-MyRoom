package com.tws.taskroom.feature_room.data.data_source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.tws.taskroom.feature_room.domain.model.MyRoom
import kotlinx.coroutines.flow.Flow


@Dao
interface RoomDao {

    @Query("SELECT * FROM room")
    fun getAllRoom(): Flow<List<MyRoom>>

    @Query("SELECT * FROM room WHERE name = :name")
    suspend fun getMyRoomByName(name: String): MyRoom

    @Insert
    suspend fun insertRoom(room: MyRoom)

    @Update
    suspend fun updateRoom(room: MyRoom)
}