package com.tws.taskroom.feature_room.presentation.rooms

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tws.taskroom.feature_room.domain.use_case.MyRoomUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MyRoomViewModel @Inject constructor(
    private val myRoomUseCase: MyRoomUseCase,
) : ViewModel() {

    private val _snackBarState = MutableStateFlow(false)
    val snackBarState = _snackBarState.asStateFlow()

    private val _uiState = MutableStateFlow(MyRoomState())
    val uiState = _uiState.asStateFlow()

    private val _eventChannel = Channel<Error>()
    val eventChannel = _eventChannel.receiveAsFlow()

    private var getAllRooms: Job? = null

    init {
        getAllRooms(true)
    }

    fun onEvent(event: MyRoomEvent) {
        when (event) {
            MyRoomEvent.Cancel -> {

            }

            MyRoomEvent.FABEvent -> {

            }

            is MyRoomEvent.IsLive -> {
                _uiState.update {
                    it.copy(isLive = event.isLive)
                }
            }

            is MyRoomEvent.OnNameChange -> {
                _uiState.update {
                    it.copy(myRoomName = event.newText)
                }
            }

            is MyRoomEvent.Create -> {
                viewModelScope.launch {
                    withContext(Dispatchers.Main) {
                        val myRoom = myRoomUseCase.getMyRoomByNameUseCase(event.myRoom.name)
                        if (myRoom == null) {
                            myRoomUseCase.insertMyRoom(event.myRoom)
                        } else {
                            _eventChannel.send(Error.ErrorMsg)
                        }
                    }
                }
            }

            is MyRoomEvent.Update -> {
                viewModelScope.launch {
                    withContext(Dispatchers.Main) {
                        myRoomUseCase.updateMyRoom(event.myRoom)
                    }
                }
            }
        }
    }

    private fun getAllRooms(isSortEnabled: Boolean) {
        getAllRooms?.cancel()
        getAllRooms = myRoomUseCase.getAllRoom(isSortEnabled)
            .onEach {
                _uiState.value = uiState.value.copy(
                    myRooms = it,
                )
            }.launchIn(viewModelScope)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertMillisToDateTimeString(timestamp: Long): String {
        val instant = Instant.ofEpochMilli(timestamp)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a")
        return localDateTime.format(formatter)
    }

}