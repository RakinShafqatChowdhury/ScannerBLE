package com.example.scannerble.helper

object Utils {
    fun getGattServiceName(uuid: String): String {
        val serviceNames = mapOf(
            "00001800-0000-1000-8000-00805f9b34fb" to "Generic Access",
            "00001801-0000-1000-8000-00805f9b34fb" to "Generic Attribute",
            "00001802-0000-1000-8000-00805f9b34fb" to "Immediate Alert",
            "00001803-0000-1000-8000-00805f9b34fb" to "Link Loss",
            "00001804-0000-1000-8000-00805f9b34fb" to "Tx Power",
            "00001805-0000-1000-8000-00805f9b34fb" to "Current Time Service",
            "00001806-0000-1000-8000-00805f9b34fb" to "Reference Time Update Service",
            "00001807-0000-1000-8000-00805f9b34fb" to "Next DST Change Service",
            "00001808-0000-1000-8000-00805f9b34fb" to "Glucose",
            "00001809-0000-1000-8000-00805f9b34fb" to "Health Thermometer",
            "0000180a-0000-1000-8000-00805f9b34fb" to "Device Information",
            "0000180d-0000-1000-8000-00805f9b34fb" to "Heart Rate Service",
            "0000180e-0000-1000-8000-00805f9b34fb" to "Phone Alert Status Service",
            "0000180f-0000-1000-8000-00805f9b34fb" to "Battery Service",
            "00001810-0000-1000-8000-00805f9b34fb" to "Blood Pressure Service",
            "00001811-0000-1000-8000-00805f9b34fb" to "Alert Notification Service",
            "00001812-0000-1000-8000-00805f9b34fb" to "Human Interface Device",
            "00001813-0000-1000-8000-00805f9b34fb" to "Scan Parameters",
            "00001814-0000-1000-8000-00805f9b34fb" to "Running Speed and Cadence",
            "00001815-0000-1000-8000-00805f9b34fb" to "Automation IO",
            "00001816-0000-1000-8000-00805f9b34fb" to "Cycling Speed and Cadence",
            "00001818-0000-1000-8000-00805f9b34fb" to "Cycling Power",
            "00001819-0000-1000-8000-00805f9b34fb" to "Location and Navigation",
            "0000181a-0000-1000-8000-00805f9b34fb" to "Environmental Sensing",
            "0000181b-0000-1000-8000-00805f9b34fb" to "Body Composition",
            "0000181c-0000-1000-8000-00805f9b34fb" to "User Data",
            "0000181d-0000-1000-8000-00805f9b34fb" to "Weight Scale",
            "0000181e-0000-1000-8000-00805f9b34fb" to "Bond Management",
            "0000181f-0000-1000-8000-00805f9b34fb" to "Continuous Glucose Monitoring",
            "00001820-0000-1000-8000-00805f9b34fb" to "Internet Protocol Support",
            "00001821-0000-1000-8000-00805f9b34fb" to "Indoor Positioning",
            "00001822-0000-1000-8000-00805f9b34fb" to "Pulse Oximeter",
            "00001823-0000-1000-8000-00805f9b34fb" to "HTTP Proxy",
            "00001824-0000-1000-8000-00805f9b34fb" to "Transport Discovery",
            "00001825-0000-1000-8000-00805f9b34fb" to "Object Transfer Service",
            "00001826-0000-1000-8000-00805f9b34fb" to "Fitness Machine",
            "00001827-0000-1000-8000-00805f9b34fb" to "Mesh Provisioning",
            "00001828-0000-1000-8000-00805f9b34fb" to "Mesh Proxy",
            "00001829-0000-1000-8000-00805f9b34fb" to "Reconnection Configuration",
            "0000182a-0000-1000-8000-00805f9b34fb" to "Insulin Delivery",
            "00001830-0000-1000-8000-00805f9b34fb" to "Binary Sensor",
            "00001831-0000-1000-8000-00805f9b34fb" to "Emergency Configuration",
            "00001832-0000-1000-8000-00805f9b34fb" to "Asset Tracking"
        )

        return serviceNames[uuid] ?: "Unknown Service"
    }
}