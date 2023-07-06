package com.tws.taskroom.feature_room.domain.use_case

import com.tws.taskroom.feature_room.domain.model.MyRoom
import com.tws.taskroom.feature_room.domain.repository.MyRoomRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllRoomUseCase(
    private val myRoomRepo: MyRoomRepo,
) {

    operator fun invoke(isSortEnabled: Boolean = false): Flow<List<MyRoom>> {
        return myRoomRepo.getAllRoom().map { myRoom ->
            if (isSortEnabled)
                myRoom.sortedBy {
                    !it.isLive
                }
            else
                myRoom
        }
    }

}