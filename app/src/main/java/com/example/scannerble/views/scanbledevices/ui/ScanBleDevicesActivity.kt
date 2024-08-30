package com.example.scannerble.views.scanbledevices.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.scannerble.R
import com.example.scannerble.databinding.ActivityScanBleDevicesBinding

class ScanBleDevicesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanBleDevicesBinding
    // Define a unique request code for permissions
    private val BLUETOOTH_PERMISSION_REQUEST_CODE = 101

    // Permissions required for Bluetooth functionality
    private val requiredPermissions = mutableListOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
    ).apply {
        // Add Bluetooth connect and scan permissions for Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add(Manifest.permission.BLUETOOTH_CONNECT)
            add(Manifest.permission.BLUETOOTH_SCAN)
        } else {
            // Add location permission for older versions
            add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }.toTypedArray()
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

        checkAndRequestBluetoothPermissions()

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
            val allPermissionsGranted = grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }

            if (allPermissionsGranted) {
                onBluetoothPermissionsGranted()
            } else {
                onBluetoothPermissionsDenied()
            }
        }
    }

    private fun onBluetoothPermissionsGranted() {
        Toast.makeText(this, "Bluetooth permissions granted", Toast.LENGTH_SHORT).show()
    }

    private fun onBluetoothPermissionsDenied() {
        Toast.makeText(this, "Bluetooth permissions denied", Toast.LENGTH_SHORT).show()
    }
}