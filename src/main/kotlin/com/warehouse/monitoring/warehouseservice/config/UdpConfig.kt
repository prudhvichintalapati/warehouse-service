package com.warehouse.monitoring.warehouseservice.config

import com.warehouse.monitoring.warehouseservice.domain.SensorMeasurement
import com.warehouse.monitoring.warehouseservice.domain.SensorType
import com.warehouse.monitoring.warehouseservice.service.WarehouseService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.ip.udp.UnicastReceivingChannelAdapter
import org.springframework.messaging.MessageChannel

private val logger = KotlinLogging.logger {}

@Configuration
class UdpConfig(
    private val warehouseService: WarehouseService
) {
    
    @Value("\${warehouse.sensors.temperature.port}")
    private var temperaturePort: Int = 3344
    
    @Value("\${warehouse.sensors.humidity.port}")
    private var humidityPort: Int = 3355
    
    // Temperature Sensor Channel and Adapter
    @Bean
    fun temperatureChannel(): MessageChannel {
        return DirectChannel()
    }
    
    @Bean
    fun temperatureUdpAdapter(): UnicastReceivingChannelAdapter {
        val adapter = UnicastReceivingChannelAdapter(temperaturePort)
        adapter.outputChannel = temperatureChannel()
        logger.info { "Temperature UDP listener started on port $temperaturePort" }
        return adapter
    }
    
    @ServiceActivator(inputChannel = "temperatureChannel")
    fun handleTemperatureMessage(message: ByteArray) {
        val rawData = String(message)
        logger.debug { "Received temperature data: $rawData" }
        
        SensorMeasurement.parse(rawData, SensorType.TEMPERATURE)?.let { measurement ->
            warehouseService.processMeasurement(measurement)
        } ?: logger.warn { "Failed to parse temperature data: $rawData" }
    }
    
    // Humidity Sensor Channel and Adapter
    @Bean
    fun humidityChannel(): MessageChannel {
        return DirectChannel()
    }
    
    @Bean
    fun humidityUdpAdapter(): UnicastReceivingChannelAdapter {
        val adapter = UnicastReceivingChannelAdapter(humidityPort)
        adapter.outputChannel = humidityChannel()
        logger.info { "Humidity UDP listener started on port $humidityPort" }
        return adapter
    }
    
    @ServiceActivator(inputChannel = "humidityChannel")
    fun handleHumidityMessage(message: ByteArray) {
        val rawData = String(message)
        logger.debug { "Received humidity data: $rawData" }
        
        SensorMeasurement.parse(rawData, SensorType.HUMIDITY)?.let { measurement ->
            warehouseService.processMeasurement(measurement)
        } ?: logger.warn { "Failed to parse humidity data: $rawData" }
    }
}
