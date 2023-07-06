package com.tws.taskroom.feature_room.domain.use_case

import com.tws.taskroom.feature_room.domain.model.MyRoom
import com.tws.taskroom.feature_room.domain.repository.MyRoomRepo

class InsertMyRoom(
    private val myRoomRepo: MyRoomRepo,
) {
    suspend operator fun invoke(room: MyRoom) {
        myRoomRepo.insertRoom(room = room)
    }
}