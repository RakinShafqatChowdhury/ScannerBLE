package com.example.scannerble.views.scanbledevices.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scannerble.R
import com.example.scannerble.databinding.ActivityScanBleDevicesBinding
import com.example.scannerble.views.connectedbledevice.ui.ConnectBleActivity
import com.example.scannerble.views.scanbledevices.adapters.BleDeviceAdapter
import com.example.scannerble.views.scanbledevices.model.ScannedBleDevice
import com.example.scannerble.views.scanbledevices.viewmodel.ScanBleViewModel


class ScanBleDevicesActivity : AppCompatActivity(), BleDeviceAdapter.ScannedBleDeviceClickListener {

    private lateinit var binding: ActivityScanBleDevicesBinding

    // Define a unique request code for permissions
    private val BLUETOOTH_PERMISSION_REQUEST_CODE = 101

    // Permissions required for Bluetooth functionality
    private val requiredPermissions = mutableListOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add(Manifest.permission.BLUETOOTH_CONNECT)
            add(Manifest.permission.BLUETOOTH_SCAN)
        }
        add(Manifest.permission.ACCESS_FINE_LOCATION)

    }.toTypedArray()

    private val bleViewModel: ScanBleViewModel by viewModels()
    private lateinit var bleDeviceAdapter: BleDeviceAdapter
    private lateinit var enableBluetoothLauncher: ActivityResultLauncher<Intent>

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

        binding.scanBleDevicesBtn.setOnClickListener {
            checkAndRequestBluetoothPermissions()
        }

        bleDeviceAdapter = BleDeviceAdapter(mutableListOf(), this)
            .apply {
                setHasStableIds(true)
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
        // Observe scanned devices
        bleViewModel.scannedDevices.observe(this) { devices ->
            bleDeviceAdapter.updateDevices(devices)

        }

        enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Bluetooth enabled", Toast.LENGTH_SHORT).show()
                bleViewModel.startScanning()
            } else {
                enableBluetoothOption()
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
                Toast.makeText(this, "Bluetooth permissions granted", Toast.LENGTH_SHORT).show()
                onBluetoothPermissionsGranted()
            } else {
                onBluetoothPermissionsDenied()
            }
        }
    }

    private fun onBluetoothPermissionsGranted() {
        if (isBluetoothEnabled()) {
            bleViewModel.startScanning()
        } else
            Toast.makeText(this, "Please turn on bluetooth", Toast.LENGTH_SHORT).show()
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

    private fun enableBluetoothOption() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        enableBluetoothLauncher.launch(enableBtIntent)
    }


    @SuppressLint("MissingPermission")
    override fun deviceClickListener(device: ScannedBleDevice) {
        if (device.isConnectable == true) {
            val intent = Intent(this, ConnectBleActivity::class.java)
            intent.putExtra("ScannedBleDevice", device)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Device not connectable", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bleViewModel.stopScanning()
    }

}