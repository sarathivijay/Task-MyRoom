package com.tws.taskroom.feature_room.domain.use_case

data class MyRoomUseCase(
    val getAllRoom: GetAllRoomUseCase,
    val insertMyRoom: InsertMyRoom,
    val updateMyRoom: UpdateMyRoom,
    val getMyRoomByNameUseCase: GetMyRoomByNameUseCase
)
