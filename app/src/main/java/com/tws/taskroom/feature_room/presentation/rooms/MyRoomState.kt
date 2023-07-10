package com.tws.taskroom.feature_room.presentation.rooms

import com.tws.taskroom.feature_room.domain.model.MyRoom

data class MyRoomState(
    val myRooms: List<MyRoom> = emptyList(),
    val myRoomName: String = "",
    val isLive: Boolean = false,
    val error: String = "",
)
sealed class MyRoomEvent {
    object FABEvent : MyRoomEvent()
    data class IsLive(val isLive: Boolean) : MyRoomEvent()
    data class OnNameChange(val newText: String) : MyRoomEvent()
    data class Create(val myRoom: MyRoom) : MyRoomEvent()
    data class Update(val myRoom: MyRoom) : MyRoomEvent()
    object Cancel : MyRoomEvent()
}

sealed class Error {
    object Idle : Error()
    object ErrorMsg : Error()
}