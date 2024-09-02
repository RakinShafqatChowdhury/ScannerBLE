package com.example.scannerble.helper

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.example.scannerble.R

object Utils {
    private var dialog: Dialog? = null

    fun getCharacteristicName(uuid: String): String {
        val characteristicName = mapOf(
            // Generic Access Profile
            "00002a00-0000-1000-8000-00805f9b34fb" to "Device Name",
            "00002a01-0000-1000-8000-00805f9b34fb" to "Appearance",
            "00002a02-0000-1000-8000-00805f9b34fb" to "Peripheral Privacy Flag",
            "00002a03-0000-1000-8000-00805f9b34fb" to "Reconnection Address",
            "00002a04-0000-1000-8000-00805f9b34fb" to "Peripheral Preferred Connection Parameters",

            // Generic Attribute Profile
            "00002a05-0000-1000-8000-00805f9b34fb" to "Service Changed",

            // Device Information
            "00002a23-0000-1000-8000-00805f9b34fb" to "System ID",
            "00002a24-0000-1000-8000-00805f9b34fb" to "Model Number String",
            "00002a25-0000-1000-8000-00805f9b34fb" to "Serial Number String",
            "00002a26-0000-1000-8000-00805f9b34fb" to "Firmware Revision String",
            "00002a27-0000-1000-8000-00805f9b34fb" to "Hardware Revision String",
            "00002a28-0000-1000-8000-00805f9b34fb" to "Software Revision String",
            "00002a29-0000-1000-8000-00805f9b34fb" to "Manufacturer Name String",
            "00002a2a-0000-1000-8000-00805f9b34fb" to "IEEE 11073-20601 Regulatory Certification Data List",
            "00002a50-0000-1000-8000-00805f9b34fb" to "PnP ID",

            // Heart Rate
            "00002a37-0000-1000-8000-00805f9b34fb" to "Heart Rate Measurement",
            "00002a38-0000-1000-8000-00805f9b34fb" to "Body Sensor Location",
            "00002a39-0000-1000-8000-00805f9b34fb" to "Heart Rate Control Point",

            // Battery Service
            "00002a19-0000-1000-8000-00805f9b34fb" to "Battery Level",

            // Health Thermometer
            "00002a1c-0000-1000-8000-00805f9b34fb" to "Temperature Measurement",
            "00002a1d-0000-1000-8000-00805f9b34fb" to "Temperature Type",
            "00002a1e-0000-1000-8000-00805f9b34fb" to "Intermediate Temperature",

            // Blood Pressure
            "00002a35-0000-1000-8000-00805f9b34fb" to "Blood Pressure Measurement",
            "00002a36-0000-1000-8000-00805f9b34fb" to "Intermediate Cuff Pressure",

            // Cycling Speed and Cadence
            "00002a5b-0000-1000-8000-00805f9b34fb" to "CSC Measurement",
            "00002a5c-0000-1000-8000-00805f9b34fb" to "CSC Feature",

            // Glucose
            "00002a18-0000-1000-8000-00805f9b34fb" to "Glucose Measurement",
            "00002a34-0000-1000-8000-00805f9b34fb" to "Glucose Measurement Context",
            "00002a51-0000-1000-8000-00805f9b34fb" to "Glucose Feature",

            // Environmental Sensing
            "00002a6e-0000-1000-8000-00805f9b34fb" to "Temperature",
            "00002a6f-0000-1000-8000-00805f9b34fb" to "Humidity",
            "00002a70-0000-1000-8000-00805f9b34fb" to "Pressure",

            // Add more known UUIDs as needed
            "00002b29-0000-1000-8000-00805f9b34fb" to "Client Supported Feature"
        )

        return characteristicName[uuid] ?: "Unknown Characteristic ($uuid)"
    }

    fun showProgressDialog(context: Context, message: String? = "Loading...") {
        if (dialog == null) {
            val builder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.custom_progress_loader, null)
            val msg = view.findViewById<TextView>(R.id.progress_message)
            msg.text =  message
            builder.setView(view)
            builder.setCancelable(false)

            dialog = builder.create()
            dialog?.show()
        }
    }

    fun dismissProgressDialog() {
        dialog?.dismiss()
        dialog = null
    }

}