package com.example.myapplication.utils.CLCalculation;

fun main() {
    val positions =
        arrayOf(doubleArrayOf(38.15,55.08), doubleArrayOf(38.17,55.05), doubleArrayOf(38.12,55.14))
    val distances = doubleArrayOf(20.0,21.8,17.2)

    val result =
        CircularLaterationCalculator.lateration(
            positions,
            distances
        )
}