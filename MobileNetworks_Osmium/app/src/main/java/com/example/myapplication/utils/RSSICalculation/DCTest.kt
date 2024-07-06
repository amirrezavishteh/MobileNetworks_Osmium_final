package com.example.myapplication.utils.RSSICalculation;

import android.util.Log

fun main() {
    val rssi = -70.0 // Example RSSI value in dBm
    val distance = RssiCalculator.calculateDistance(rssi)
    println("distance is $distance meters")
}