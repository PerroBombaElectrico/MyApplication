package com.example.miprueba

/**
 * IoTConnectivity contains helper functions for Bluetooth BLE and Wi-Fi transmission simulation,
 * sensor data collection, and actuator command execution.
 */
class IoTConnectivity {

    fun sendBluetoothData(data: String): String {
        return "BLE transmission successful: $data"
    }

    fun sendWifiData(data: String): String {
        return "Wi-Fi transmission successful: $data"
    }

    fun getSensorData(): List<Pair<String, String>> {
        return listOf(
            Pair("Temperature", "24.5 °C"),
            Pair("Humidity", "60 %"),
            Pair("Pressure", "1013.25 hPa"),
            Pair("Vibration", "0.02 mm/s")
        )
    }

    fun sendActuatorCommand(actuator: String, value: String): String {
        return "Actuator $actuator set to $value"
    }

    companion object {
        fun sendBluetoothData(data: String): String = IoTConnectivity().sendBluetoothData(data)
        fun sendWifiData(data: String): String = IoTConnectivity().sendWifiData(data)
        fun getSensorData(): List<Pair<String, String>> = IoTConnectivity().getSensorData()
        fun sendActuatorCommand(actuator: String, value: String): String = IoTConnectivity().sendActuatorCommand(actuator, value)
    }
}

fun sendBluetoothData(data: String): String = IoTConnectivity.sendBluetoothData(data)
fun sendWifiData(data: String): String = IoTConnectivity.sendWifiData(data)
fun getSensorData(): List<Pair<String, String>> = IoTConnectivity.getSensorData()
fun sendActuatorCommand(actuator: String, value: String): String = IoTConnectivity.sendActuatorCommand(actuator, value)
