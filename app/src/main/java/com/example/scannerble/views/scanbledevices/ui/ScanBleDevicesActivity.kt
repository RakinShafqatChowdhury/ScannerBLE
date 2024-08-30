package com.example.scannerble.views.scanbledevices.ui

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scannerble.R
import com.example.scannerble.databinding.ActivityScanBleDevicesBinding
import com.example.scannerble.views.scanbledevices.adapters.BleDeviceAdapter
import com.example.scannerble.views.scanbledevices.model.ScannedBleDevice
import com.example.scannerble.views.scanbledevices.viewmodel.BleViewModel


class ScanBleDevicesActivity : AppCompatActivity(), BleDeviceAdapter.ScannedBleDeviceClickListener {

    private lateinit var binding: ActivityScanBleDevicesBinding

    // Define a unique request code for permissions
    private val BLUETOOTH_PERMISSION_REQUEST_CODE = 101

    // Permissions required for Bluetooth functionality
    private val requiredPermissions = mutableListOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
    ).apply {
        add(Manifest.permission.BLUETOOTH_CONNECT)
        add(Manifest.permission.BLUETOOTH_SCAN)
        add(Manifest.permission.ACCESS_FINE_LOCATION)

    }.toTypedArray()

    private lateinit var bleViewModel: BleViewModel
    private lateinit var bleDeviceAdapter: BleDeviceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityScanBleDevicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bleViewModel = ViewModelProvider(this)[BleViewModel::class.java]

        binding.scanBleDevicesBtn.setOnClickListener {
            checkAndRequestBluetoothPermissions()
        }

        // Observe scanned devices
        bleViewModel.scannedDevices.observe(this) { devices ->
            bleDeviceAdapter =
                BleDeviceAdapter(mutableListOf(), this)
                    .apply {
                        setHasStableIds(true)
                        updateDevices(devices)
                    }
            binding.scannedDevicesListRV.apply {
                setHasFixedSize(true)
                itemAnimator = null
                layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                adapter = bleDeviceAdapter
            }
        }

    }


    private fun checkAndRequestBluetoothPermissions() {
        val missingPermissions = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            // Request missing permissions
            ActivityCompat.requestPermissions(
                this,
                missingPermissions.toTypedArray(),
                BLUETOOTH_PERMISSION_REQUEST_CODE
            )
        } else {
            // All permissions are already granted
            onBluetoothPermissionsGranted()
        }
    }

    // Handle the permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == BLUETOOTH_PERMISSION_REQUEST_CODE) {
            // Check if all permissions are granted
            val allPermissionsGranted =
                grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }

            if (allPermissionsGranted) {
                onBluetoothPermissionsGranted()
            } else {
                onBluetoothPermissionsDenied()
            }
        }
    }

    private fun onBluetoothPermissionsGranted() {
        Toast.makeText(this, "Bluetooth permissions granted", Toast.LENGTH_SHORT).show()
        if (isBluetoothEnabled()) {
            Toast.makeText(this, "Bluetooth is enabled", Toast.LENGTH_SHORT).show()
            bleViewModel.stopScanning()
            bleViewModel.startScanning()
        } else
            Toast.makeText(this, "Bluetooth is disabled", Toast.LENGTH_SHORT).show()
    }

    private fun onBluetoothPermissionsDenied() {
        Toast.makeText(this, "Bluetooth permissions denied", Toast.LENGTH_SHORT).show()
    }

    private fun isBluetoothEnabled(): Boolean {
        val bluetoothManager =
            baseContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        return bluetoothAdapter.isEnabled
    }

    @SuppressLint("MissingPermission")
    override fun deviceClickListener(device: ScannedBleDevice) {
        Toast.makeText(this, "Device clicked: ${device.device?.name}", Toast.LENGTH_SHORT).show()
    }
}