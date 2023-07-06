package com.tws.taskroom.feature_room.domain.use_case

import com.tws.taskroom.feature_room.domain.model.MyRoom
import com.tws.taskroom.feature_room.domain.repository.MyRoomRepo

class GetMyRoomByNameUseCase(
    private val myRoomRepo: MyRoomRepo,
) {
    suspend operator fun invoke(name: String): MyRoom? {
        return myRoomRepo.getMyRoomByName(name = name)
    }
}