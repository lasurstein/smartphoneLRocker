package com.example.smartphonelrocker.timer

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "timers")
data class Timer(
    @PrimaryKey(autoGenerate = true) var id: Long,
    var name: String,
    var hour: Int,
    var min: Int,
    var time: String,
) {

}