package com.example.scannerble.views.scanbledevices.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.scannerble.views.scanbledevices.model.ScannedBleDevice
import com.example.scannerble.views.scanbledevices.repository.ScanBleRepository
import kotlinx.coroutines.launch

class ScanBleViewModel(application: Application) : AndroidViewModel(application) {

    private val scanBleRepository = ScanBleRepository(application)

    val scannedDevices: LiveData<List<ScannedBleDevice>> = scanBleRepository.scannedDevices

    fun startScanning() {
        viewModelScope.launch {
            scanBleRepository.startScanning()
        }
    }

    fun stopScanning() {
        viewModelScope.launch {
            scanBleRepository.stopScanning()
        }
    }
}
