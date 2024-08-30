package com.example.scannerble.views.scanbledevices.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.scannerble.R
import com.example.scannerble.views.scanbledevices.model.ScannedBleDevice

class BleDeviceAdapter(
    private var devices: List<ScannedBleDevice>,
    private val deviceClickListener: ScannedBleDeviceClickListener
) : RecyclerView.Adapter<BleDeviceAdapter.BleDeviceViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleDeviceViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.ble_devices_item, parent, false)
        return BleDeviceViewHolder(view)
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: BleDeviceViewHolder, position: Int) {
        val device = devices[position]
        holder.deviceNameTextView.text = device.device?.name ?: "Unknown"
        holder.deviceAddressTextView.text = device.device?.address
        holder.deviceRSSITextView.text = "${device.rssi.toString()} dBM"
        holder.deviceDataStatus.text = device.dataStatus.toString()
        holder.deviceConnectionStatus.text = device.isConnectable.toString()
        holder.deviceAdvertisingInterval.text = device.periodicAdvertisingInterval.toString()

        holder.itemView.setOnClickListener {
            deviceClickListener.deviceClickListener(device)
        }
    }


    inner class BleDeviceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val deviceNameTextView: TextView = view.findViewById(R.id.bleDeviceName)
        val deviceAddressTextView: TextView = view.findViewById(R.id.bleDeviceAddress)
        val deviceRSSITextView: TextView = view.findViewById(R.id.bleDeviceRssi)
        val deviceDataStatus: TextView = view.findViewById(R.id.bleDeviceDataStatus)
        val deviceConnectionStatus: TextView = view.findViewById(R.id.bleDeviceConnectable)
        val deviceAdvertisingInterval: TextView =
            view.findViewById(R.id.bleDeviceAdvertisingInterval)
    }

    override fun getItemCount(): Int = devices.size

    fun updateDevices(newDevices: List<ScannedBleDevice>) {
        val diffResult =
            DiffUtil.calculateDiff(ScannedBleDeviceDiffUtil(devices, newDevices))
        devices = newDevices
        diffResult.dispatchUpdatesTo(this)
    }


    interface ScannedBleDeviceClickListener {
        fun deviceClickListener(device: ScannedBleDevice)
    }
}
