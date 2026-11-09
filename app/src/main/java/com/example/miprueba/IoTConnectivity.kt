package com.example.miprueba

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

/**
 * Tipos de sensores sopados en el sistema IoT
 */
enum class SensorType {
    TEMPERATURE,
    HUMIDITY,
    PRESSURE,
    VIBRATION
}

/**
 * Modelo de datos estructurado para lecturas de sensores conforme a arquitectura IoT
 */
data class SensorReading(
    val id: String,
    val name: String,
    val value: String,
    val unit: String,
    val type: SensorType,
    val timestamp: String = getCurrentTimestamp(),
    val isNormalStatus: Boolean = true
)

/**
 * Modos de operación para actuadores IoT
 */
enum class ActuatorMode(val displayName: String) {
    AUTOMATIC("Automático"),
    MANUAL("Manual"),
    ECO("Eco")
}

/**
 * Comando de control estructurado para actuadores
 */
data class ActuatorCommand(
    val actuatorName: String = "Bomba de Agua Principal",
    val isEnabled: Boolean,
    val powerPercentage: Int,
    val mode: ActuatorMode
)

/**
 * Protocolos de transmisión inalámbrica soportados
 */
enum class TransmissionProtocol(val displayName: String) {
    BLUETOOTH_BLE("Bluetooth BLE"),
    WIFI("Wi-Fi TCP/IP")
}

/**
 * Resultado estructurado de una transmisión IoT
 */
data class TransmissionResult(
    val isSuccess: Boolean,
    val protocol: TransmissionProtocol,
    val message: String,
    val timestamp: String = getCurrentTimestamp()
)

private fun getCurrentTimestamp(): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date())
}

/**
 * IoTConnectivity gestiona la conectividad IoT, simulación de sensores en tiempo real vía Flow
 * y transmisión inalámbrica por Bluetooth BLE y Wi-Fi.
 */
class IoTConnectivity {

    /**
     * Retorna las lecturas estáticas iniciales (compatibilidad).
     */
    fun getSensorData(): List<Pair<String, String>> {
        return getSensorReadingsSnapshot().map { Pair(it.name, "${it.value} ${it.unit}") }
    }

    /**
     * Retorna una instantánea de lecturas estructuradas.
     */
    fun getSensorReadingsSnapshot(): List<SensorReading> {
        val timestamp = getCurrentTimestamp()
        return listOf(
            SensorReading("S1", "Temperatura Ambiente", "24.5", "°C", SensorType.TEMPERATURE, timestamp),
            SensorReading("S2", "Humedad Relativa", "58.0", "%", SensorType.HUMIDITY, timestamp),
            SensorReading("S3", "Presión Atmosférica", "1013.2", "hPa", SensorType.PRESSURE, timestamp),
            SensorReading("S4", "Vibración de Motor", "0.02", "mm/s", SensorType.VIBRATION, timestamp)
        )
    }

    /**
     * Emite un flujo (Flow) continuo de lecturas de sensores con variaciones simula en tiempo real cada 3 segundos.
     */
    fun getSensorDataFlow(): Flow<List<SensorReading>> = flow {
        while (true) {
            val timestamp = getCurrentTimestamp()
            val temp = String.format(Locale.US, "%.1f", 23.5 + Random.nextDouble(-1.5, 2.5))
            val hum = String.format(Locale.US, "%.1f", 55.0 + Random.nextDouble(-3.0, 5.0))
            val press = String.format(Locale.US, "%.1f", 1012.0 + Random.nextDouble(-2.0, 3.0))
            val vibVal = 0.01 + Random.nextDouble(0.00, 0.04)
            val vib = String.format(Locale.US, "%.2f", vibVal)

            val readings = listOf(
                SensorReading("S1", "Temperatura Ambiente", temp, "°C", SensorType.TEMPERATURE, timestamp),
                SensorReading("S2", "Humedad Relativa", hum, "%", SensorType.HUMIDITY, timestamp),
                SensorReading("S3", "Presión Atmosférica", press, "hPa", SensorType.PRESSURE, timestamp),
                SensorReading("S4", "Vibración de Motor", vib, "mm/s", SensorType.VIBRATION, timestamp, isNormalStatus = vibVal < 0.04)
            )
            emit(readings)
            delay(3000)
        }
    }

    /**
     * Transmite un comando a un actuador de forma asíncrona simulando latencia de canal.
     */
    suspend fun sendActuatorCommandAsync(
        command: ActuatorCommand,
        protocol: TransmissionProtocol
    ): TransmissionResult {
        delay(if (protocol == TransmissionProtocol.BLUETOOTH_BLE) 400 else 250)

        val stateText = if (command.isEnabled) "ON (${command.powerPercentage}%)" else "OFF"
        val payload = "[${command.actuatorName}] Modo: ${command.mode.displayName} | Estado: $stateText"

        val responseMessage = when (protocol) {
            TransmissionProtocol.BLUETOOTH_BLE -> "Transmisión BLE exitosa: $payload"
            TransmissionProtocol.WIFI -> "Transmisión Wi-Fi exitosa: $payload"
        }

        return TransmissionResult(
            isSuccess = true,
            protocol = protocol,
            message = responseMessage
        )
    }

    // Métodos estáticos/compatibilidad
    fun sendBluetoothData(data: String): String = "Transmisión BLE exitosa: $data"
    fun sendWifiData(data: String): String = "Transmisión Wi-Fi exitosa: $data"
    fun sendActuatorCommand(actuator: String, value: String): String = "Actuador $actuator configurado en $value"

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
