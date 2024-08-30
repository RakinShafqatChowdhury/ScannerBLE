package com.example.scannerble.views.scanbledevices.repository

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.scannerble.views.scanbledevices.model.ScannedBleDevice

class BleRepository(private val context: Context) {

    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private val bleScanner = bluetoothAdapter.bluetoothLeScanner
    private val _scannedDevices = MutableLiveData<List<ScannedBleDevice>>()
    val scannedDevices: LiveData<List<ScannedBleDevice>> get() = _scannedDevices

    private val _connectionStatus = MutableLiveData<Boolean>()
    val connectionStatus: LiveData<Boolean> get() = _connectionStatus

    private val devices: MutableList<ScannedBleDevice> = mutableListOf()

    init {
        _scannedDevices.value = devices
    }

    fun startScanning() {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Log.e("BLE", "Bluetooth is not enabled or not available")
            return
        }

        val bleScanner = bluetoothAdapter.bluetoothLeScanner
        if (bleScanner == null) {
            Log.e("BLE", "BLE Scanner is null")
            return
        }

        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("BLE", "Permissions not granted")
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
            val existingDeviceIndex = devices.indexOfFirst { it.device?.address == scannedDevice.device?.address }
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
            Log.e("BLE", "Scan failed with error: $errorCode")
        }
    }

    /*    @SuppressLint("MissingPermission")
        fun connectToDevice(device: BluetoothDevice) {
            bluetoothGatt = device.connectGatt(context, false, bluetoothGattCallback)
        }

        private val bluetoothGattCallback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    _connectionStatus.postValue(true)
                    gatt?.discoverServices()
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    _connectionStatus.postValue(false)
                    bluetoothGatt?.close()
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    gatt?.services?.forEach { service ->
                        Log.d("BLE", "Service discovered: ${service.uuid}")
                        service.characteristics.forEach { characteristic ->
                            readCharacteristic(gatt, characteristic)
                        }
                    }
                }
            }

            override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    characteristic?.let {
                        Log.d("BLE", "Characteristic read: ${it.uuid}, value: ${it.value}")
                    }
                }
            }
        }

        private fun readCharacteristic(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            gatt.readCharacteristic(characteristic)
        }*/
}
