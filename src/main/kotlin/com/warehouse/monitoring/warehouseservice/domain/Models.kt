package com.warehouse.monitoring.warehouseservice.domain

import java.time.LocalDateTime

enum class SensorType {
    TEMPERATURE,
    HUMIDITY
}

data class SensorMeasurement(
    val sensorId: String,
    val value: Double,
    val type: SensorType,
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        private val SENSOR_PATTERN = Regex("""sensor_id=(\w+);\s*value=([\d.]+)""")
        
        fun parse(rawData: String, type: SensorType): SensorMeasurement? {
            return SENSOR_PATTERN.find(rawData.trim())?.let { matchResult ->
                val (sensorId, value) = matchResult.destructured
                SensorMeasurement(
                    sensorId = sensorId,
                    value = value.toDouble(),
                    type = type
                )
            }
        }
    }
}

data class Alarm(
    val sensorId: String,
    val sensorType: SensorType,
    val currentValue: Double,
    val threshold: Double,
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    fun getMessage(): String {
        return "🚨 ALARM TRIGGERED! " +
                "Sensor: $sensorId | " +
                "Type: $sensorType | " +
                "Current Value: $currentValue | " +
                "Threshold: $threshold | " +
                "Time: $timestamp"
    }
}
