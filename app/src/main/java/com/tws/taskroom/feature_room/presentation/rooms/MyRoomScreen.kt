package com.tws.taskroom.feature_room.presentation.rooms

import SimpleCheckboxComponent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyRoomScreen(
    viewModel: MyRoomViewModel = viewModel(),
) {
    var isEditable by remember { mutableStateOf(false) }
    var btnCreate by remember { mutableStateOf("Create") }
    var isBottomScreenVisible by remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()
    val snackBarHostState = scaffoldState.snackbarHostState
    val uiState = viewModel.uiState.collectAsState()
    val errorState = viewModel.eventChannel
    val snackBarState by viewModel.snackBarState.collectAsState()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )

    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(10.dp)
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 13.dp),
                    text = stringResource(R.string.enter_the_room_name),
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    value = uiState.value.myRoomName,
                    readOnly = !isEditable,
                    onValueChange = {
                        viewModel.onEvent(MyRoomEvent.OnNameChange(newText = it))
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    shape = RoundedCornerShape(15.dp),
                    colors = androidx.compose.material.TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    textStyle = TextStyle(
                        fontSize = 16.sp
                    )
                )
                SimpleCheckboxComponent(
                    modifier = Modifier,
                    isChecked = uiState.value.isLive,
                    onCheckChange = {
                        viewModel.onEvent(MyRoomEvent.IsLive(it))
                    }
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isBottomScreenVisible = false
                                focusManager.clearFocus()
                                modalSheetState.hide()
                            }
                            viewModel.onEvent(MyRoomEvent.IsLive(false))
                            viewModel.onEvent(MyRoomEvent.OnNameChange(""))
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = Color.Black,
                        ),
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isBottomScreenVisible = false
                                focusManager.clearFocus()
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
                                } else {
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
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colorScheme.onSurface,
                            contentColor = Color.Black,
                        ),
                    ) {
                        Text(text = btnCreate)
                    }
                }
            }
        }
    ) {
        Scaffold(
            backgroundColor = MaterialTheme.colorScheme.background,
            floatingActionButton = {
                FloatingActionButton(
                    backgroundColor = MaterialTheme.colorScheme.onSurface,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 2.dp
                    ),
                    onClick = {
                        btnCreate = "Create"
                        isEditable = true
                        isBottomScreenVisible = true
                        viewModel.onEvent(MyRoomEvent.IsLive(false))
                        viewModel.onEvent(MyRoomEvent.OnNameChange(""))
                        coroutineScope.launch { modalSheetState.show() }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState, modifier = Modifier.padding(16.dp))
            }
        ) {
            LaunchedEffect(key1 = snackBarState) {
                errorState.collect {
                    when (it) {
                        Error.ErrorMsg -> {
                            scaffoldState.snackbarHostState.showSnackbar("Name should be unique")
                        }

                        Error.Idle -> {}
                    }
                }
            }
            LaunchedEffect(key1 = modalSheetState.currentValue) {
                isBottomScreenVisible = modalSheetState.isVisible
            }
            AnimatedVisibility(
                visible = !isBottomScreenVisible,
                enter = fadeIn(
                    animationSpec = spring(stiffness = Spring.StiffnessHigh),
                    initialAlpha = 0f,
                ),
                exit = fadeOut(
                    animationSpec = spring(stiffness = Spring.StiffnessHigh),
                    targetAlpha = 0f,
                ),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier.padding(10.dp),
                            text = stringResource(R.string.vpm),
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = 22.sp,
                                color = Color.White
                            )
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp)
                        ) {
                            Button(
                                onClick = { },
                                shape = RoundedCornerShape(30.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colorScheme.background,
                                    contentColor = MaterialTheme.colorScheme.onBackground
                                )
                            ) {
                                Text(text = "Videos")
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Button(
                                onClick = { },
                                shape = RoundedCornerShape(30.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colorScheme.onSurface,
                                    contentColor = Color.Black
                                )
                            ) {
                                Text(text = "Feeds")
                            }
                        }
                        if (uiState.value.myRooms.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    modifier = Modifier.align(Alignment.Center),
                                    text = stringResource(R.string.no_data),
                                    style = TextStyle(
                                        color = Color.White,
                                        fontSize = 18.sp
                                    )
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(it)
                                    .padding(bottom = 10.dp)
                            ) {
                                items(uiState.value.myRooms) { myRoom ->
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Card(modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(15.dp)
                                            .clip(shape = RoundedCornerShape(12.dp))
                                            .clickable {
                                                btnCreate = "Update"
                                                isEditable = false
                                                viewModel.onEvent(
                                                    MyRoomEvent.OnNameChange(
                                                        myRoom.name
                                                    )
                                                )
                                                viewModel.onEvent(MyRoomEvent.IsLive(myRoom.isLive))
                                                coroutineScope.launch { modalSheetState.show() }
                                            }) {
                                            Box(
                                                modifier = Modifier
                                                    .height(170.dp)
                                                    .fillMaxWidth()
                                                    .background(MaterialTheme.colorScheme.surface)
                                            ) {
                                                Row(
                                                    horizontalArrangement = Arrangement.End,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(10.dp)
                                                ) {
                                                    AnimatedVisibility(visible = myRoom.isLive) {
                                                        Text(
                                                            modifier = Modifier
                                                                .background(Color.Red)
                                                                .padding(horizontal = 5.dp),
                                                            text = stringResource(R.string.live),
                                                            style = TextStyle(
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color.White
                                                            )
                                                        )
                                                    }
                                                }
                                                Image(
                                                    modifier = Modifier.align(Alignment.Center),
                                                    painter = painterResource(id = R.drawable.video_play),
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                modifier = Modifier
                                                    .padding(horizontal = 20.dp)
                                                    .weight(1f),
                                                text = myRoom.name,
                                                style = TextStyle(
                                                    color = Color(0xFFB1B1B1),
                                                    fontWeight = FontWeight.Normal,
                                                    fontSize = 18.sp
                                                )
                                            )

                                            Text(
                                                modifier = Modifier
                                                    .padding(horizontal = 20.dp)
                                                    .weight(1f),
                                                text = viewModel.convertMillisToDateTimeString(
                                                    myRoom.timestamp
                                                ),
                                                style = TextStyle(
                                                    color = Color(0xFFB1B1B1),
                                                    fontWeight = FontWeight.Normal,
                                                    fontSize = 16.sp
                                                )
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(5.dp))
                                        Divider(
                                            modifier = Modifier.padding(horizontal = 20.dp),
                                            color = Color(0xFF727272).copy(alpha = 0.5f)
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


