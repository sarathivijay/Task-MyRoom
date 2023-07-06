package com.tws.taskroom.feature_room.domain.repository

import com.tws.taskroom.feature_room.domain.model.MyRoom
import kotlinx.coroutines.flow.Flow

interface MyRoomRepo {

    fun getAllRoom() : Flow<List<MyRoom>>

    suspend fun insertRoom(room: MyRoom)

    suspend fun updateRoom(room: MyRoom)

    suspend fun getMyRoomByName(name: String) : MyRoom?
}