package com.tws.taskroom.feature_room.presentation.rooms

import SimpleCheckboxComponent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tws.taskroom.R
import com.tws.taskroom.feature_room.domain.model.MyRoom
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyRoomScreen(
    viewModel: MyRoomViewModel = viewModel(),
) {
    var isEditable by remember { mutableStateOf(false) }

    val scaffoldState = rememberScaffoldState()
    val snackBarHostState = scaffoldState.snackbarHostState
    val uiState = viewModel.uiState.collectAsState()
    val snackBarState by viewModel.snackBarState.collectAsState()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )

    val coroutineScope = rememberCoroutineScope()


    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.enter_the_room_name),
                    style = TextStyle(
                        fontWeight = FontWeight.W600,
                        fontSize = 18.sp
                    )
                )
                Spacer(modifier = Modifier.height(5.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.value.myRoomName,
                    readOnly = !isEditable,
                    onValueChange = {
                        viewModel.onEvent(MyRoomEvent.OnNameChange(newText = it))
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    )
                )
                SimpleCheckboxComponent(
                    isChecked = uiState.value.isLive,
                    onCheckChange = {
                        viewModel.onEvent(MyRoomEvent.IsLive(it))
                    }, modifier = Modifier
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                modalSheetState.hide()
                            }
                            viewModel.onEvent(MyRoomEvent.IsLive(false))
                            viewModel.onEvent(MyRoomEvent.OnNameChange(""))
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Gray,
                            contentColor = Color.White,
                        ),
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                modalSheetState.hide()
                            }
                            if (uiState.value.myRoomName.isEmpty()) {
                                coroutineScope.launch {
                                    snackBarHostState.showSnackbar(message = "Name cannot be empty")
                                }
                            } else {
                                if (!isEditable) {
                                    viewModel.onEvent(
                                        MyRoomEvent.Update(
                                            myRoom = MyRoom(
                                                name = uiState.value.myRoomName,
                                                timestamp = System.currentTimeMillis(),
                                                isLive = uiState.value.isLive
                                            )
                                        )
                                    )
                                }
                                viewModel.onEvent(
                                    MyRoomEvent.Create(
                                        myRoom = MyRoom(
                                            name = uiState.value.myRoomName,
                                            timestamp = System.currentTimeMillis(),
                                            isLive = uiState.value.isLive
                                        )
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Red,
                            contentColor = Color.White,
                        ),
                    ) {
                        Text(text = stringResource(R.string.create))
                    }
                }
            }
        }
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    isEditable = true
                    viewModel.onEvent(MyRoomEvent.IsLive(false))
                    viewModel.onEvent(MyRoomEvent.OnNameChange(""))
                    coroutineScope.launch { modalSheetState.show() }
                }) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState, modifier = Modifier.padding(16.dp))
            }
        ) {
            LaunchedEffect(key1 = snackBarState) {
                if (snackBarState) {
                    scaffoldState.snackbarHostState.showSnackbar("Name should be unique")
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray.copy(alpha = 0.5f))
            ) {
                LazyColumn(
                    modifier = Modifier.padding(it)
                ) {
                    items(uiState.value.myRooms) { myRoom ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                                .clickable {
                                    isEditable = false
                                    viewModel.onEvent(MyRoomEvent.OnNameChange(myRoom.name))
                                    viewModel.onEvent(MyRoomEvent.IsLive(myRoom.isLive))
                                    coroutineScope.launch { modalSheetState.show() }
                                }

                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White)
                                        .padding(15.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = myRoom.name,
                                            style = TextStyle(
                                                fontWeight = FontWeight.W600,
                                                fontSize = 18.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(5.dp))
                                        Text(
                                            text = convertMillisToDateTimeString(myRoom.timestamp),
                                            style = TextStyle(
                                                fontSize = 16.sp
                                            )
                                        )
                                    }
                                    AnimatedVisibility(visible = myRoom.isLive) {
                                        Text(
                                            text = stringResource(R.string.live).uppercase(),
                                            style = TextStyle(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF196619)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun convertMillisToDateTimeString(timestamp: Long): String {
    val instant = Instant.ofEpochMilli(timestamp)
    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a")
    return localDateTime.format(formatter)
}


