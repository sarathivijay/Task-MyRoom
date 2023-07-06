package com.tws.taskroom.feature_room.data.repository

import com.tws.taskroom.feature_room.data.data_source.RoomDao
import com.tws.taskroom.feature_room.domain.model.MyRoom
import com.tws.taskroom.feature_room.domain.repository.MyRoomRepo
import kotlinx.coroutines.flow.Flow

class MyRoomRepoImpl(
    private val roomDao: RoomDao
) : MyRoomRepo {

    override fun getAllRoom(): Flow<List<MyRoom>> {
        return roomDao.getAllRoom()
    }

    override suspend fun insertRoom(room: MyRoom) {
        roomDao.insertRoom(room = room)
    }

    override suspend fun updateRoom(room: MyRoom) {
        roomDao.updateRoom(room = room)
    }

    override suspend fun getMyRoomByName(name: String) : MyRoom? {
        return roomDao.getMyRoomByName(name)
    }
}