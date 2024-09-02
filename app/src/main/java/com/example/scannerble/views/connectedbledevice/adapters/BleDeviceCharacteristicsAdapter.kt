package com.example.scannerble.views.connectedbledevice.adapters

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.scannerble.R
import com.example.scannerble.helper.Utils

class BleDeviceCharacteristicsAdapter(
    private var characteristicsList: MutableList<BluetoothGattCharacteristic>,
    private val readWriteClickListener: CharacteristicReadWriteClickListener
) : RecyclerView.Adapter<BleDeviceCharacteristicsAdapter.BleDeviceViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleDeviceViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.ble_device_services_item, parent, false)
        return BleDeviceViewHolder(view)
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onBindViewHolder(holder: BleDeviceViewHolder, position: Int) {
        val characteristic = characteristicsList[position]
        holder.bleServiceName.text = Utils.getCharacteristicName(characteristic.uuid.toString())

        val properties = characteristic.properties

        val isReadable = properties and BluetoothGattCharacteristic.PROPERTY_READ != 0
        val isWritable =
            properties and (BluetoothGattCharacteristic.PROPERTY_WRITE or BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0
        val isWritableNoResponse =
            properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE != 0


        holder.bleDeviceCharacteristicsReadWriteLayout.visibility =
            if (isReadable || isWritable) View.VISIBLE else View.GONE

        holder.bleServiceAddress.text = when {
            isReadable && isWritable && isWritableNoResponse -> "Readable,Writable,Write No Response"
            isReadable && isWritable -> "Readable,Writable"
            isReadable -> "Readable"
            isWritable -> "Writable"
            else -> "Read, Write not allowed"
        }

        holder.readButton.visibility = if (isReadable) View.VISIBLE else View.GONE
        holder.writeButton.visibility = if (isWritable) View.VISIBLE else View.GONE

        holder.readButton.setOnClickListener {
            readWriteClickListener.readClickListener(characteristic, holder.bleServiceName.text.toString())
        }
        holder.writeButton.setOnClickListener {
            readWriteClickListener.writeClickListener(characteristic)
        }

    }


    inner class BleDeviceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bleServiceName: TextView = view.findViewById(R.id.bleDeviceServiceName)
        val bleServiceAddress: TextView = view.findViewById(R.id.bleDeviceServiceAddress)
        val readButton: TextView = view.findViewById(R.id.readValueBtn)
        val writeButton: TextView = view.findViewById(R.id.writeValueBtn)
        val bleDeviceCharacteristicsReadWriteLayout: View =
            view.findViewById(R.id.bleDeviceCharacteristicsReadWriteLayout)
    }

    override fun getItemCount(): Int = characteristicsList.size

    override fun getItemId(position: Int): Long {
        return characteristicsList[position].uuid.hashCode().toLong()
    }

    interface CharacteristicReadWriteClickListener {
        fun readClickListener(characteristicItem: BluetoothGattCharacteristic, toString: String)
        fun writeClickListener(characteristicItem: BluetoothGattCharacteristic)
    }
}
