package com.example.scannerble.views.connectedbledevice.viewmodel

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.scannerble.views.connectedbledevice.repository.ConnectBleRepository

class ConnectBleViewModel(application: Application) : AndroidViewModel(application) {

    private val bleRepository: ConnectBleRepository = ConnectBleRepository(application)

    val services: LiveData<List<BluetoothGattService>?> = bleRepository.services
    val connectionState: LiveData<String> = bleRepository.connectionState


    fun connectToDevice(device: BluetoothDevice) {
        bleRepository.connectToDevice(device)
    }

    // Delegate disconnecting to repository
    fun disconnect() {
        bleRepository.disconnect()
    }
}
