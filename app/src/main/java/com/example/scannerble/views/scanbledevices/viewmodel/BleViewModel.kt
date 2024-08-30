package com.example.scannerble.views.scanbledevices.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.scannerble.views.scanbledevices.model.ScannedBleDevice
import com.example.scannerble.views.scanbledevices.repository.BleRepository

class BleViewModel(application: Application) : AndroidViewModel(application) {

    private val bleRepository = BleRepository(application)

    val scannedDevices: LiveData<List<ScannedBleDevice>> = bleRepository.scannedDevices
    val connectionStatus: LiveData<Boolean> = bleRepository.connectionStatus

    fun startScanning() {
        bleRepository.startScanning()
    }

    fun stopScanning() {
        bleRepository.stopScanning()
    }
}
