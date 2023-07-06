package com.tws.taskroom.feature_room.presentation.rooms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tws.taskroom.feature_room.domain.use_case.MyRoomUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MyRoomViewModel @Inject constructor(
    private val myRoomUseCase: MyRoomUseCase,
) : ViewModel() {

    private val _snackBarState = MutableStateFlow(false)
    val snackBarState = _snackBarState.asStateFlow()

    private val _uiState = MutableStateFlow(MyRoomState())
    val uiState = _uiState.asStateFlow()

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
                            _snackBarState.value = true
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

}