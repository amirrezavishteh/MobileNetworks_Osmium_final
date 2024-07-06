package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "signal",
)
data class Signal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cellId: Int,
    val rssi: Int,
    val tac:Int,
    val mcc:String,
    val mnc:String,
    val x:Double,
    val y:Double
)