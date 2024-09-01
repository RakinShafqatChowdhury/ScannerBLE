package com.example.scannerble.views.scanbledevices.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.scannerble.views.scanbledevices.model.ScannedBleDevice
import com.example.scannerble.views.scanbledevices.repository.ScanBleRepository
import kotlinx.coroutines.launch

class ScanBleViewModel(application: Application) : AndroidViewModel(application) {

    private val bleRepository = ScanBleRepository(application)

    val scannedDevices: LiveData<List<ScannedBleDevice>> = bleRepository.scannedDevices

    fun startScanning() {
        viewModelScope.launch {
            bleRepository.startScanning()
        }
    }

    fun stopScanning() {
        viewModelScope.launch {
            bleRepository.stopScanning()
        }
    }
}
