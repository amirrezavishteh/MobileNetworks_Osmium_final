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
fun CellsPage(cells: List<Cell>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(cells, key = { it.cellId }) {
            CellInfoCard(it)
        }
    }
}

@Composable
fun CellInfoCard(cellInfo: Cell) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Cell Id: ${cellInfo.cellId}")
            Text(text = "x: ${cellInfo.x}")
            Text(text = "y: ${cellInfo.y}")
        }
    }
}