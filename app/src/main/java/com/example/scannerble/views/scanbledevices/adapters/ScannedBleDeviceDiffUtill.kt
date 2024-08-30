package com.example.scannerble.views.scanbledevices.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.scannerble.views.scanbledevices.model.ScannedBleDevice

class ScannedBleDeviceDiffUtil(
    private val oldList: List<ScannedBleDevice>,
    private val newList: List<ScannedBleDevice>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].device?.address == newList[newItemPosition].device?.address
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}