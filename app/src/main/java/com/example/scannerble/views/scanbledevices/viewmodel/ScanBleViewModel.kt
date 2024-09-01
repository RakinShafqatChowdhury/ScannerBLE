package com.example.scannerble.views.scanbledevices.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.scannerble.views.scanbledevices.model.ScannedBleDevice
import com.example.scannerble.views.scanbledevices.repository.ScanBleRepository

class ScanBleViewModel(application: Application) : AndroidViewModel(application) {

    private val bleRepository = ScanBleRepository(application)

    val scannedDevices: LiveData<List<ScannedBleDevice>> = bleRepository.scannedDevices

    fun startScanning() {
        bleRepository.startScanning()
    }

    fun stopScanning() {
        bleRepository.stopScanning()
    }
}
