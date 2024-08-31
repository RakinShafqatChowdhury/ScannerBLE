package com.example.scannerble.views.connectedbledevice.adapters

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.scannerble.R
import com.example.scannerble.helper.Utils

class BleDeviceServiceAdapter(
    private var services: List<BluetoothGattService>,
    private val serviceClickListener: BleServiceClickListener
) : RecyclerView.Adapter<BleDeviceServiceAdapter.BleDeviceViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleDeviceViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.ble_device_services_item, parent, false)
        return BleDeviceViewHolder(view)
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: BleDeviceViewHolder, position: Int) {
        val service = services[position]
        holder.bleServiceName.text = Utils.getGattServiceName(service.uuid.toString())
        holder.bleServiceAddress.text = if (service.type == 0) "Primary Type" else "Secondary Type"

        holder.itemView.setOnClickListener {
            serviceClickListener.serviceClickListener(services[position].characteristics)
        }
    }


    inner class BleDeviceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bleServiceName: TextView = view.findViewById(R.id.bleDeviceServiceName)
        val bleServiceAddress: TextView = view.findViewById(R.id.bleDeviceServiceAddress)
    }

    override fun getItemCount(): Int = services.size

    override fun getItemId(position: Int): Long {
        return services[position].uuid.hashCode().toLong()
    }


    interface BleServiceClickListener {
        fun serviceClickListener(characteristicItems: List<BluetoothGattCharacteristic>)
    }
}
