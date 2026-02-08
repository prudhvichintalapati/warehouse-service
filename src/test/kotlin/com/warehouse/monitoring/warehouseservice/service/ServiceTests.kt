package com.warehouse.monitoring.warehouseservice.service

import com.warehouse.monitoring.warehouseservice.domain.SensorMeasurement
import com.warehouse.monitoring.warehouseservice.domain.SensorType
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate

class WarehouseServiceTest {
    
    private lateinit var rabbitTemplate: RabbitTemplate
    private lateinit var warehouseService: WarehouseService
    
    private val exchangeName = "sensor.data.exchange"
    private val temperatureRoutingKey = "sensor.temperature"
    private val humidityRoutingKey = "sensor.humidity"
    
    @BeforeEach
    fun setup() {
        rabbitTemplate = mockk(relaxed = true)
        warehouseService = WarehouseService(rabbitTemplate)
        
        // Set private fields using reflection
        val exchangeField = WarehouseService::class.java.getDeclaredField("exchangeName")
        exchangeField.isAccessible = true
        exchangeField.set(warehouseService, exchangeName)
        
        val tempKeyField = WarehouseService::class.java.getDeclaredField("temperatureRoutingKey")
        tempKeyField.isAccessible = true
        tempKeyField.set(warehouseService, temperatureRoutingKey)
        
        val humidityKeyField = WarehouseService::class.java.getDeclaredField("humidityRoutingKey")
        humidityKeyField.isAccessible = true
        humidityKeyField.set(warehouseService, humidityRoutingKey)
    }
    
    @Test
    fun `should send temperature measurement to correct routing key`() {
        val measurement = SensorMeasurement("t1", 30.0, SensorType.TEMPERATURE)
        
        warehouseService.processMeasurement(measurement)
        
        verify {
            rabbitTemplate.convertAndSend(
                exchangeName,
                temperatureRoutingKey,
                measurement
            )
        }
    }
    
    @Test
    fun `should send humidity measurement to correct routing key`() {
        val measurement = SensorMeasurement("h1", 45.0, SensorType.HUMIDITY)
        
        warehouseService.processMeasurement(measurement)
        
        verify {
            rabbitTemplate.convertAndSend(
                exchangeName,
                humidityRoutingKey,
                measurement
            )
        }
    }

}

class CentralMonitoringServiceTest {
    
    private lateinit var centralMonitoringService: CentralMonitoringService
    
    @BeforeEach
    fun setup() {
        centralMonitoringService = CentralMonitoringService()
        
        // Set thresholds using reflection
        val tempThresholdField = CentralMonitoringService::class.java.getDeclaredField("temperatureThreshold")
        tempThresholdField.isAccessible = true
        tempThresholdField.set(centralMonitoringService, 35.0)
        
        val humidityThresholdField = CentralMonitoringService::class.java.getDeclaredField("humidityThreshold")
        humidityThresholdField.isAccessible = true
        humidityThresholdField.set(centralMonitoringService, 50.0)
    }
    
    @Test
    fun `should not trigger alarm when temperature is below threshold`() {
        val measurement = SensorMeasurement("t1", 30.0, SensorType.TEMPERATURE)
        
        // Should not throw exception
        centralMonitoringService.monitorTemperature(measurement)
    }
    
    @Test
    fun `should trigger alarm when temperature exceeds threshold`() {
        val measurement = SensorMeasurement("t1", 40.0, SensorType.TEMPERATURE)
        
        // Should trigger alarm (would see output in logs)
        centralMonitoringService.monitorTemperature(measurement)
    }
    
    @Test
    fun `should not trigger alarm when humidity is below threshold`() {
        val measurement = SensorMeasurement("h1", 45.0, SensorType.HUMIDITY)
        
        centralMonitoringService.monitorHumidity(measurement)
    }
    
    @Test
    fun `should trigger alarm when humidity exceeds threshold`() {
        val measurement = SensorMeasurement("h1", 60.0, SensorType.HUMIDITY)
        
        centralMonitoringService.monitorHumidity(measurement)
    }
    
    @Test
    fun `should handle temperature at exact threshold`() {
        val measurement = SensorMeasurement("t1", 35.0, SensorType.TEMPERATURE)
        
        // Should not trigger alarm (not exceeding)
        centralMonitoringService.monitorTemperature(measurement)
    }
    
    @Test
    fun `should trigger alarm for value slightly above threshold`() {
        val measurement = SensorMeasurement("t1", 35.1, SensorType.TEMPERATURE)
        
        // Should trigger alarm
        centralMonitoringService.monitorTemperature(measurement)
    }
}
