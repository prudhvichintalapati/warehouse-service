package com.warehouse.monitoring.warehouseservice.service

import com.warehouse.monitoring.warehouseservice.domain.SensorMeasurement
import com.warehouse.monitoring.warehouseservice.domain.SensorType
import mu.KotlinLogging
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class WarehouseService(
    private val rabbitTemplate: RabbitTemplate
) {
    
    @Value("\${messaging.exchange.sensor-data}")
    private lateinit var exchangeName: String
    
    @Value("\${messaging.routing-key.temperature}")
    private lateinit var temperatureRoutingKey: String
    
    @Value("\${messaging.routing-key.humidity}")
    private lateinit var humidityRoutingKey: String
    
    fun processMeasurement(measurement: SensorMeasurement) {
        logger.info { 
            "📊 Processing ${measurement.type} measurement: " +
            "Sensor=${measurement.sensorId}, Value=${measurement.value}" 
        }
        
        val routingKey = when (measurement.type) {
            SensorType.TEMPERATURE -> temperatureRoutingKey
            SensorType.HUMIDITY -> humidityRoutingKey
        }
        
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, measurement)
            logger.debug { "Measurement sent to central monitoring service via $routingKey" }
        } catch (e: Exception) {
            logger.error(e) { "Failed to send measurement to message broker" }
        }
    }
}
