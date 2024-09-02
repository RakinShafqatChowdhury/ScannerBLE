package com.example.scannerble.views.connectedbledevice.ui

import android.Manifest
import android.bluetooth.BluetoothGattCharacteristic
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scannerble.R
import com.example.scannerble.databinding.ActivityConnectBleBinding
import com.example.scannerble.helper.Utils
import com.example.scannerble.views.connectedbledevice.adapters.BleDeviceCharacteristicsAdapter
import com.example.scannerble.views.connectedbledevice.viewmodel.ConnectBleViewModel
import com.example.scannerble.views.scanbledevices.model.ScannedBleDevice

class ConnectBleActivity : AppCompatActivity(),
    BleDeviceCharacteristicsAdapter.CharacteristicReadWriteClickListener {

    private lateinit var binding: ActivityConnectBleBinding
    private val connectBleViewModel: ConnectBleViewModel by viewModels()
    private lateinit var bleDeviceCharacteristicsAdapter: BleDeviceCharacteristicsAdapter
    private val characteristicsList = mutableListOf<BluetoothGattCharacteristic>()

    private var readValueHeader: String = ""

    private var startReadingTime: Long = 0
    private var endReadingTime: Long = 0

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

        val selectedDevice: ScannedBleDevice? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("ScannedBleDevice", ScannedBleDevice::class.java)
            } else {
                @Suppress("DEPRECATION") // Suppress the deprecation warning for older API usage
                intent.getParcelableExtra("ScannedBleDevice") as? ScannedBleDevice
            }

        if (selectedDevice != null) {
            Utils.showProgressDialog(this)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            binding.bleDeviceName.text = selectedDevice.device?.name ?: "Unknown"
            binding.bleDeviceAddress.text = selectedDevice.device?.address

            selectedDevice.device?.let { connectBleViewModel.connectToDevice(it) }
        }


        connectBleViewModel.connectionState.observe(this) { connectionState ->
            if (connectionState == "Connected") {
                binding.connectStatus.setTextColor(ContextCompat.getColor(this, R.color.green_700))
                binding.bleDeviceImage.setImageResource(R.drawable.ic_bluetooth_connected)
                Toast.makeText(this, "Device Connected", Toast.LENGTH_SHORT).show()
            } else {
                binding.connectStatus.setTextColor(ContextCompat.getColor(this, R.color.red_700))
                Toast.makeText(this, "Device Disconnected", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({ finish() }, 1000)
            }
            binding.connectStatus.text = connectionState
        }

        connectBleViewModel.services.observe(this) { services ->
            if (services != null) {

                for (service in services) {
                    for (characteristic in service.characteristics) {
                        characteristicsList.add(characteristic)
                    }
                }

                bleDeviceCharacteristicsAdapter =
                    BleDeviceCharacteristicsAdapter(characteristicsList, this)
                        .apply {
                            setHasStableIds(true)
                        }
                binding.scannedDeviceServicesListRV.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(
                        context,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    adapter = bleDeviceCharacteristicsAdapter
                }
            } else {
                Toast.makeText(this, "Services not discovered", Toast.LENGTH_SHORT).show()
            }
            Utils.dismissProgressDialog()
        }

        connectBleViewModel.characteristicValue.observe(this) { value ->
            endReadingTime = SystemClock.elapsedRealtime()
            val elapsedTimeInSec = (endReadingTime - startReadingTime)/1000.0
            val bytesPerSec = value?.toByteArray()?.size?.div(elapsedTimeInSec)
            val formattedBytesPerSec = String.format("%.2f", bytesPerSec ?: 0.0)
            Utils.dismissProgressDialog()
            showValueDialog(value, formattedBytesPerSec)
        }

        connectBleViewModel.writeCharacteristicResponse.observe(this) { response ->
            Utils.dismissProgressDialog()
            Toast.makeText(this, "$response", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showValueDialog(value: String?, bytesPerSec: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(readValueHeader.ifEmpty { "Value" })
        builder.setMessage("$value\n\n(Data Transfer Speed: $bytesPerSec bytes/sec)")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun showDialogForDataWrite(characteristicItem: BluetoothGattCharacteristic) {
        val dialogView: View = LayoutInflater.from(this).inflate(R.layout.write_data_layout, null)

        val editText = dialogView.findViewById<EditText>(R.id.writeDataET)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Send") { dialogInterface, _ ->
                Utils.showProgressDialog(this)
                val input = editText.text.toString().toByteArray()
                connectBleViewModel.writeCharacteristic(
                    characteristicItem,
                    input,
                    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                )
                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        connectBleViewModel.disconnect()
    }

    override fun readClickListener(
        characteristicItem: BluetoothGattCharacteristic,
        characteristicName: String
    ) {
        Utils.showProgressDialog(this)
        readValueHeader = characteristicName
        startReadingTime = SystemClock.elapsedRealtime()
        connectBleViewModel.readCharacteristic(
            characteristicItem.service.uuid,
            characteristicItem.uuid
        )
    }

    override fun writeClickListener(characteristicItem: BluetoothGattCharacteristic) {
        showDialogForDataWrite(characteristicItem)
    }
}