package com.example.scannerble.views.scanbledevices.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.scannerble.R
import com.example.scannerble.views.scanbledevices.model.ScannedBleDevice

class BleDeviceAdapter(
    private var devices: MutableList<ScannedBleDevice>,
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

        when {
            device.rssi!! > -50 -> {
                holder.deviceSignalImageView.setBackgroundResource(R.drawable.ic_strong_signal)
                holder.deviceImage.setColorFilter(Color.parseColor("#268906"))
            }
            device.rssi in -70..-51 -> {
                holder.deviceSignalImageView.setBackgroundResource(R.drawable.ic_medium_signal)
                holder.deviceImage.setColorFilter(Color.parseColor("#E5DB35"))
            }
            else -> {
                holder.deviceSignalImageView.setBackgroundResource(R.drawable.ic_weak_signal)
                holder.deviceImage.setColorFilter(Color.parseColor("#E53535"))
            }
        }
        holder.itemView.setOnClickListener {
            deviceClickListener.deviceClickListener(device)
        }
    }


    inner class BleDeviceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val deviceNameTextView: TextView = view.findViewById(R.id.bleDeviceName)
        val deviceAddressTextView: TextView = view.findViewById(R.id.bleDeviceAddress)
        val deviceRSSITextView: TextView = view.findViewById(R.id.bleDeviceRssi)
        val deviceSignalImageView: ImageView = view.findViewById(R.id.bleDeviceSignalImage)
        val deviceImage: ImageView = view.findViewById(R.id.bleDeviceImage)
    }

    override fun getItemCount(): Int = devices.size

    override fun getItemId(position: Int): Long {
        return devices.getOrNull(position)?.device?.address?.hashCode()?.toLong() ?: 0
    }

    fun updateDevices(newDevices: List<ScannedBleDevice>) {
        val diffResult =
            DiffUtil.calculateDiff(ScannedBleDeviceDiffUtil(devices, newDevices))
        devices.clear()
        devices.addAll(newDevices)
        diffResult.dispatchUpdatesTo(this)
    }


    interface ScannedBleDeviceClickListener {
        fun deviceClickListener(device: ScannedBleDevice)
    }
}
