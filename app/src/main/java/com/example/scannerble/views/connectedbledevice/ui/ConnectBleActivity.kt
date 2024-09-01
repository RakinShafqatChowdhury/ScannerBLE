package com.example.scannerble.views.connectedbledevice.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scannerble.R
import com.example.scannerble.databinding.ActivityConnectBleBinding
import com.example.scannerble.views.connectedbledevice.adapters.BleDeviceCharacteristicsAdapter
import com.example.scannerble.views.connectedbledevice.viewmodel.ConnectBleViewModel
import com.example.scannerble.views.scanbledevices.model.ScannedBleDevice

class ConnectBleActivity : AppCompatActivity(),
    BleDeviceCharacteristicsAdapter.CharacteristicReadWriteClickListener {

    private lateinit var binding: ActivityConnectBleBinding
    private val connectBleViewModel: ConnectBleViewModel by viewModels()
    private lateinit var bleDeviceCharacteristicsAdapter: BleDeviceCharacteristicsAdapter
    private val characteristicsList = mutableListOf<BluetoothGattCharacteristic>()

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
                finish()
            }
            binding.connectStatus.text = it
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
        }

        connectBleViewModel.characteristicValue.observe(this) {
            showValueDialog(it)
        }

        connectBleViewModel.writeCharacteristicResponse.observe(this) {
            Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
        }

    }

    private fun showValueDialog(value: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Value")
        builder.setMessage(value)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun showDialogForDataWrite(characteristicItem: BluetoothGattCharacteristic) {
        val dialogView: View = LayoutInflater.from(this).inflate(R.layout.write_data_layout, null)

        val editText = dialogView.findViewById<EditText>(R.id.writeDataET)

        // Build the dialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Send") { dialogInterface, _ ->

                val input = editText.text.toString().toByteArray()
                connectBleViewModel.writeCharacteristic(characteristicItem, input, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
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

    override fun readClickListener(characteristicItem: BluetoothGattCharacteristic) {
        connectBleViewModel.readCharacteristic(
            characteristicItem.service.uuid,
            characteristicItem.uuid
        )
    }

    override fun writeClickListener(characteristicItem: BluetoothGattCharacteristic) {
        showDialogForDataWrite(characteristicItem)
    }
}