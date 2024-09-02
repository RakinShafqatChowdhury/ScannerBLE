package com.example.scannerble.views.connectedbledevice.repository

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.SystemClock
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.UUID

class ConnectBleRepository(private val context: Context) {

    private var bluetoothGatt: BluetoothGatt? = null

    private val _services = MutableLiveData<List<BluetoothGattService>?>()
    val services: LiveData<List<BluetoothGattService>?> get() = _services

    private val _connectionState = MutableLiveData<String>()
    val connectionState: LiveData<String> get() = _connectionState

    private val _characteristicValue = MutableLiveData<String?>()
    val characteristicValue: LiveData<String?> = _characteristicValue

    private val _writeCharacteristicResponse = MutableLiveData<String?>()
    val writeCharacteristicResponse: LiveData<String?> = _writeCharacteristicResponse

    fun connectToDevice(device: BluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    _connectionState.postValue("Connected")
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

                        return
                    }
                    gatt.discoverServices()
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    _connectionState.postValue("Disconnected")
                    bluetoothGatt?.close()
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                _services.postValue(gatt.services)
            } else {
                _services.postValue(null)
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    val value = characteristic?.value?.let { byteArray ->
                        if (byteArray.isNotEmpty()) {
                            String(byteArray, Charsets.UTF_8)
                        } else {
                            "Empty or null value"
                        }
                    } ?: "Unknown"

                    _characteristicValue.postValue(value)
                }

                BluetoothGatt.GATT_INSUFFICIENT_AUTHORIZATION -> {
                    _characteristicValue.postValue("Insufficient authorization")
                }

                else -> {
                    _characteristicValue.postValue("Value read failed")
                }
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                _writeCharacteristicResponse.postValue("Value written successfully")
            } else {
                _writeCharacteristicResponse.postValue("Value write failed")
            }
        }
    }

    fun readCharacteristicValue(serviceUuid: UUID, characteristicUuid: UUID) {
        bluetoothGatt?.let { gatt ->
            val service = gatt.getService(serviceUuid)
            val characteristic = service?.getCharacteristic(characteristicUuid)
            if (characteristic != null) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                gatt.readCharacteristic(characteristic)
            }
        }
    }

    fun writeToCharacteristic(
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray,
        writeType: Int
    ) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        if (bluetoothGatt != null && bluetoothGatt!!.connect())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bluetoothGatt?.writeCharacteristic(characteristic, value, writeType)
            } else {
                characteristic.value = value
                bluetoothGatt?.writeCharacteristic(characteristic)
            }
        else
            _writeCharacteristicResponse.postValue("Gatt service not connected")
    }

    fun disconnect() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        bluetoothGatt?.close()
        bluetoothGatt = null
        _connectionState.postValue("Disconnected")
    }
}
