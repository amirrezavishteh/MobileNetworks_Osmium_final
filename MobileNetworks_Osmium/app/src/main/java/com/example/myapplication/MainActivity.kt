package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.CellInfo
import android.telephony.CellInfoLte
import android.telephony.TelephonyManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.utils.CLCalculation.CircularLaterationCalculator
import com.example.myapplication.utils.RSSICalculation.RssiCalculator
import com.example.osmium.data.db.SignalDao
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var location: Pair<Double, Double>? = null
    private val cells = MutableStateFlow<List<Cell?>>(emptyList())
    private val signals = MutableStateFlow<List<Signal>>(emptyList())

    private lateinit var  signalsDao :SignalDao

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        signalsDao=AppDatabase.getInstance(this).signalDao

        getDeviceCurrentLocation(this)
        getCellInfo(this)

        setContent {
            MyApplicationTheme {
                val cellsState by cells.collectAsStateWithLifecycle()
                val signalsState by signals.collectAsStateWithLifecycle()

                var tabIndex by remember { mutableIntStateOf(0) }
                val tabs = listOf("سلول ها", "سیگنال های دریافتی")

                Column(modifier = Modifier.fillMaxWidth()) {
                    TabRow(selectedTabIndex = tabIndex) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                text = { Text(title) },
                                selected = tabIndex == index,
                                onClick = { tabIndex = index },
                            )
                        }
                    }
                    when (tabIndex) {
                        0 -> CellsPage(cellsState.filterNotNull())
                        1 -> SignalsPage(signalsState)
                    }
                }
            }
        }

        lifecycleScope.launch {
            signalsDao.getSignals().collectLatest { s ->
                signals.update { s }
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            signalsDao.getSignals()
                .collectLatest { signals ->
                    val cellInfos=signals.groupBy { it.cellId }.map {
                        if (it.value.size<3) return@map null

                        val positions = Array(3) { DoubleArray(2)}
                        for(i:Int in positions.indices) {
                            positions[i][0]=it.value[i].x
                            positions[i][1]=it.value[i].y
                        }

                        val distances = DoubleArray(3)
                        for(i:Int in distances.indices) {
                            distances[i]=RssiCalculator.calculateDistance(it.value[i].rssi.toDouble())
                        }
                        positions.forEach { it.forEach { it2 -> Log.d("mhr","$it2") } }
                        val cellPosition=  CircularLaterationCalculator.estimatePosition(positions, distances)

                        Cell(
                            cellId = it.value[0].cellId,
                            x=cellPosition[0],
                            y=cellPosition[1]
                        )
                    }
                    cells.update { cellInfos }
                }
        }
    }

    private fun getDeviceCurrentLocation(activity: MainActivity) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener {
                    location = Pair(it.latitude, it.longitude)
                }
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun getCellInfo(activity: MainActivity) {
        lifecycleScope.launch(Dispatchers.IO) {
            val telephonyManager =
                activity.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

            if (telephonyManager == null) return@launch

            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.INTERNET
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                while (true) {
                    val signals = mutableListOf<Signal>()

                    val deviceX = location?.first
                    val deviceY = location?.second
                    if (deviceX != null && deviceY != null) {
                        val cellInfoList = telephonyManager.allCellInfo
                        for (cellInfo in cellInfoList) {
                            if (cellInfo is CellInfoLte) {
                                val rssi = cellInfo.cellSignalStrength.rssi
                                if (rssi != CellInfo.UNAVAILABLE) {
                                    signals.add(
                                        Signal(
                                            cellId = cellInfo.cellIdentity.ci,
                                            rssi = cellInfo.cellSignalStrength.rssi,
                                            tac = cellInfo.cellIdentity.tac,
                                            mnc = cellInfo.cellIdentity.mncString ?: "",
                                            mcc = cellInfo.cellIdentity.mccString ?: "",
                                            x = deviceX,
                                            y = deviceY
                                        )
                                    )
                                }
                            }
                        }
                        signalsDao.insertAll(signals)
                    }

                    delay(5000)
                }
            }
        }
    }
}