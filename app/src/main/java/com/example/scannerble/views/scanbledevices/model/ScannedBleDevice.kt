package com.example.scannerble.views.scanbledevices.model

import android.bluetooth.BluetoothDevice
import android.os.Parcel
import android.os.Parcelable

data class ScannedBleDevice(
    var device: BluetoothDevice? = null,
    var rssi: Int? = null,
    var timestampNanos: Long? = null,
    var isLegacy: Boolean? = null,
    var isConnectable: Boolean? = null,
    var dataStatus: Int? = null,
    var primaryPhy: Int? = null,
    var secondaryPhy: Int? = null,
    var advertisingSid: Int? = null,
    var txPower: Int? = null,
    var periodicAdvertisingInterval: Int? = null
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(BluetoothDevice::class.java.classLoader),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(device, flags)
        parcel.writeValue(rssi)
        parcel.writeValue(timestampNanos)
        parcel.writeValue(isLegacy)
        parcel.writeValue(isConnectable)
        parcel.writeValue(dataStatus)
        parcel.writeValue(primaryPhy)
        parcel.writeValue(secondaryPhy)
        parcel.writeValue(advertisingSid)
        parcel.writeValue(txPower)
        parcel.writeValue(periodicAdvertisingInterval)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScannedBleDevice> {
        override fun createFromParcel(parcel: Parcel): ScannedBleDevice {
            return ScannedBleDevice(parcel)
        }

        override fun newArray(size: Int): Array<ScannedBleDevice?> {
            return arrayOfNulls(size)
        }
    }
}
