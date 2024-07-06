package com.example.myapplication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SignalsPage(signals: List<Signal>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(signals, key = { it.id }) {
            SignalInfoCard(it)
        }
    }
}

@Composable
fun SignalInfoCard(signalInfo: Signal) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Cell Id: ${signalInfo.cellId}")
            Text(text = "tac: ${signalInfo.tac}")
            Text(text = "mcc: ${signalInfo.mcc}")
            Text(text = "mnc: ${signalInfo.mnc}")
            Text(text = "rssi: ${signalInfo.rssi}")
        }
    }
}