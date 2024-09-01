package com.example.scannerble.views.connectedbledevice.viewmodel

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.scannerble.views.connectedbledevice.repository.ConnectBleRepository
import kotlinx.coroutines.launch
import java.util.UUID

class ConnectBleViewModel(application: Application) : AndroidViewModel(application) {

    private val bleRepository: ConnectBleRepository = ConnectBleRepository(application)

    val services: LiveData<List<BluetoothGattService>?> = bleRepository.services
    val connectionState: LiveData<String> = bleRepository.connectionState
    val characteristicValue: LiveData<String?> = bleRepository.characteristicValue


    fun connectToDevice(device: BluetoothDevice) {
        viewModelScope.launch {
            bleRepository.connectToDevice(device)
        }
    }

    // Delegate disconnecting to repository
    fun disconnect() {
        viewModelScope.launch {
            bleRepository.disconnect()
        }
    }

    fun readCharacteristic(serviceUuid: UUID, characteristicUuid: UUID) {
        viewModelScope.launch {
            bleRepository.readCharacteristicValue(serviceUuid, characteristicUuid)
        }
    }

}
