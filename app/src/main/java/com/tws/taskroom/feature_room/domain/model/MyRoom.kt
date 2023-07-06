package com.tws.taskroom.feature_room.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "room")
data class MyRoom(
    @PrimaryKey(autoGenerate = false)
    val name: String,
    val timestamp: Long,
    val isLive: Boolean = false,
)
