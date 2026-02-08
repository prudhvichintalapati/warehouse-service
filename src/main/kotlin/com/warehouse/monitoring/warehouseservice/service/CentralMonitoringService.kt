package com.warehouse.monitoring.warehouseservice.service

import com.warehouse.monitoring.warehouseservice.domain.Alarm
import com.warehouse.monitoring.warehouseservice.domain.SensorMeasurement
import com.warehouse.monitoring.warehouseservice.domain.SensorType
import mu.KotlinLogging
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class CentralMonitoringService {
    
    @Value("\${warehouse.sensors.temperature.threshold}")
    private var temperatureThreshold: Double = 35.0
    
    @Value("\${warehouse.sensors.humidity.threshold}")
    private var humidityThreshold: Double = 50.0
    
    @RabbitListener(queues = ["\${messaging.queue.temperature}"])
    fun monitorTemperature(measurement: SensorMeasurement) {
        logger.info { 
            "🌡️  Temperature Reading: ${measurement.sensorId} = ${measurement.value}°C " +
            "(Threshold: ${temperatureThreshold}°C)" 
        }
        
        if (measurement.value > temperatureThreshold) {
            val alarm = Alarm(
                sensorId = measurement.sensorId,
                sensorType = SensorType.TEMPERATURE,
                currentValue = measurement.value,
                threshold = temperatureThreshold,
                timestamp = measurement.timestamp
            )
            triggerAlarm(alarm)
        }
    }
    
    @RabbitListener(queues = ["\${messaging.queue.humidity}"])
    fun monitorHumidity(measurement: SensorMeasurement) {
        logger.info { 
            "💧 Humidity Reading: ${measurement.sensorId} = ${measurement.value}% " +
            "(Threshold: ${humidityThreshold}%)" 
        }
        
        if (measurement.value > humidityThreshold) {
            val alarm = Alarm(
                sensorId = measurement.sensorId,
                sensorType = SensorType.HUMIDITY,
                currentValue = measurement.value,
                threshold = humidityThreshold,
                timestamp = measurement.timestamp
            )
            triggerAlarm(alarm)
        }
    }
    
    private fun triggerAlarm(alarm: Alarm) {
        println("\n" + "=".repeat(80))
        println(alarm.getMessage())
        println("=".repeat(80) + "\n")
        logger.error { alarm.getMessage() }
    }
}
