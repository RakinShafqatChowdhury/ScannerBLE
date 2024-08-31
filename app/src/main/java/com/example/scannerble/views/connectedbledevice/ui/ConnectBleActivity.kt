package com.example.scannerble.views.connectedbledevice.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scannerble.R
import com.example.scannerble.databinding.ActivityConnectBleBinding
import com.example.scannerble.views.connectedbledevice.adapters.BleDeviceServiceAdapter
import com.example.scannerble.views.connectedbledevice.viewmodel.ConnectBleViewModel
import com.example.scannerble.views.scanbledevices.model.ScannedBleDevice

class ConnectBleActivity : AppCompatActivity(), BleDeviceServiceAdapter.BleServiceClickListener {

    private lateinit var binding: ActivityConnectBleBinding
    private val connectBleViewModel: ConnectBleViewModel by viewModels()
    private lateinit var bleDeviceServiceAdapter: BleDeviceServiceAdapter

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityConnectBleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val obj: ScannedBleDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("ScannedBleDevice", ScannedBleDevice::class.java)
        } else {
            @Suppress("DEPRECATION") // Suppress the deprecation warning for older API usage
            intent.getParcelableExtra("ScannedBleDevice") as? ScannedBleDevice
        }

        if (obj != null) {
            binding.bleDeviceName.text = obj.device?.name
            binding.bleDeviceAddress.text = obj.device?.address
            obj.device?.let { connectBleViewModel.connectToDevice(it) }
        }


        connectBleViewModel.connectionState.observe(this) {
            if (it == "Connected") {
                binding.connectStatus.setTextColor(ContextCompat.getColor(this, R.color.green_700))
                binding.bleDeviceImage.setImageResource(R.drawable.ic_bluetooth_connected)
                Toast.makeText(this, "Device Connected", Toast.LENGTH_SHORT).show()
            } else {
                binding.connectStatus.setTextColor(ContextCompat.getColor(this, R.color.red_700))
                Toast.makeText(this, "Device Disconnected", Toast.LENGTH_SHORT).show()
            }
            binding.connectStatus.text = it
        }

        connectBleViewModel.services.observe(this) {
            if (it != null) {
                Toast.makeText(this, "Services discovered", Toast.LENGTH_SHORT).show()

                bleDeviceServiceAdapter = BleDeviceServiceAdapter(it, this)
                    .apply {
                        setHasStableIds(true)
                    }
                binding.scannedDeviceServicesListRV.apply {
                    setHasFixedSize(true)
                    itemAnimator = null
                    layoutManager = LinearLayoutManager(
                        context,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    adapter = bleDeviceServiceAdapter
                }
            } else {
                Toast.makeText(this, "Services not discovered", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        connectBleViewModel.disconnect()
    }

    override fun serviceClickListener(characteristicItems: List<BluetoothGattCharacteristic>) {

    }
}