package com.example.scannerble.views.connectedbledevice.viewmodel

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.scannerble.views.connectedbledevice.repository.ConnectBleRepository
import kotlinx.coroutines.launch
import java.util.UUID

class ConnectBleViewModel(application: Application) : AndroidViewModel(application) {

    private val connectBleRepository: ConnectBleRepository = ConnectBleRepository(application)

    val services: LiveData<List<BluetoothGattService>?> = connectBleRepository.services
    val connectionState: LiveData<String> = connectBleRepository.connectionState
    val characteristicValue: LiveData<String?> = connectBleRepository.characteristicValue
    val writeCharacteristicResponse: LiveData<String?> = connectBleRepository.writeCharacteristicResponse


    fun connectToDevice(device: BluetoothDevice) {
        viewModelScope.launch {
            connectBleRepository.connectToDevice(device)
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            connectBleRepository.disconnect()
        }
    }

    fun readCharacteristic(serviceUuid: UUID, characteristicUuid: UUID) {
        viewModelScope.launch {
            connectBleRepository.readCharacteristicValue(serviceUuid, characteristicUuid)
        }
    }

    fun writeCharacteristic(
        characteristic: BluetoothGattCharacteristic,
        input: ByteArray,
        writeTypeDefault: Int
    ) {
        viewModelScope.launch {
            connectBleRepository.writeToCharacteristic(characteristic, input, writeTypeDefault)
        }
    }

}
