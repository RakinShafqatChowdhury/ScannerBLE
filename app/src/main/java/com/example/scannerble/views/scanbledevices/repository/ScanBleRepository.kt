package com.example.scannerble.views.scanbledevices.repository

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.scannerble.views.scanbledevices.model.ScannedBleDevice

class ScanBleRepository(private val context: Context) {

    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private val bleScanner = bluetoothAdapter.bluetoothLeScanner
    private val _scannedDevices = MutableLiveData<List<ScannedBleDevice>>()
    val scannedDevices: LiveData<List<ScannedBleDevice>> get() = _scannedDevices

    private val devices: MutableList<ScannedBleDevice> = mutableListOf()

    init {
        _scannedDevices.value = devices
    }

    fun startScanning() {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Toast.makeText(context, "Bluetooth is not enabled or not available", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val bleScanner = bluetoothAdapter.bluetoothLeScanner
        if (bleScanner == null) {
            Log.e("BLE", "BLE Scanner is null")
            return
        }

        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context, "Permissions not granted", Toast.LENGTH_SHORT).show()
            return
        }
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        bleScanner.startScan(listOf<ScanFilter>(), scanSettings, bleScanCallback)

    }

    fun stopScanning() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        bleScanner.stopScan(bleScanCallback)
    }

    private val bleScanCallback = object : ScanCallback() {
        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {

            val scannedDevice = ScannedBleDevice().apply {
                device = result.device
                rssi = result.rssi
                timestampNanos = result.timestampNanos
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    isConnectable = result.isConnectable
                    isLegacy = result.isLegacy
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    txPower = result.txPower
                }
                dataStatus = result.dataStatus
                periodicAdvertisingInterval = result.periodicAdvertisingInterval

            }
            // Update the device list
            val existingDeviceIndex =
                devices.indexOfFirst { it.device?.address == scannedDevice.device?.address }
            if (existingDeviceIndex != -1) {
                // Update existing device
                devices[existingDeviceIndex] = scannedDevice
            } else {
                // Add new device
                devices.add(scannedDevice)
            }
            _scannedDevices.postValue(devices)
        }

        override fun onScanFailed(errorCode: Int) {
            Toast.makeText(context, "Scan failed", Toast.LENGTH_SHORT).show()
        }
    }
}
