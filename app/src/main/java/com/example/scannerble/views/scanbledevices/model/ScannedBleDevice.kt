package com.example.scannerble.views.scanbledevices.model

import android.bluetooth.BluetoothDevice

data class ScannedBleDevice(
    var device: BluetoothDevice? = null,
    var rssi: Int? = null,
    var timestampNanos: Long? = null,
    var isLegacy: Boolean? = null,
    var isConnectable: Boolean? = null,
    var dataStatus: Int? = null,
    var primaryPhy: Int? = null,
    var secondaryPhy: Int? = null,
    var advertisingSid: Int? = null,
    var txPower: Int? = null,
    var periodicAdvertisingInterval: Int? = null
)
