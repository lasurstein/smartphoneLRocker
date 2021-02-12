package com.example.smartphonelrocker.timer

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timers")
data class MyTimer(
    @PrimaryKey(autoGenerate = true) var id: Long,
    var name: String,
    var hour: Int,
    var min: Int,
    var time: String,
) {
}