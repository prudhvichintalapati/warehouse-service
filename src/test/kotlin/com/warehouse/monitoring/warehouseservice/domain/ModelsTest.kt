package com.warehouse.monitoring.warehouseservice.domain

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SensorMeasurementTest {
    
    @Test
    fun `should parse valid temperature measurement`() {
        val rawData = "sensor_id=t1; value=30"
        val measurement = SensorMeasurement.parse(rawData, SensorType.TEMPERATURE)
        
        assertNotNull(measurement)
        assertEquals("t1", measurement?.sensorId)
        Assertions.assertEquals(30.0, measurement?.value)
        assertEquals(SensorType.TEMPERATURE, measurement?.type)
    }
    
    @Test
    fun `should parse valid humidity measurement`() {
        val rawData = "sensor_id=h1; value=40.5"
        val measurement = SensorMeasurement.parse(rawData, SensorType.HUMIDITY)
        
        assertNotNull(measurement)
        assertEquals("h1", measurement?.sensorId)
        Assertions.assertEquals(40.5, measurement?.value)
        assertEquals(SensorType.HUMIDITY, measurement?.type)
    }
    
    @Test
    fun `should parse measurement with extra spaces`() {
        val rawData = "sensor_id=t2;   value=25.75"
        val measurement = SensorMeasurement.parse(rawData, SensorType.TEMPERATURE)
        
        assertNotNull(measurement)
        assertEquals("t2", measurement?.sensorId)
        Assertions.assertEquals(25.75, measurement?.value)
    }
    
    @Test
    fun `should return null for invalid format`() {
        val rawData = "invalid data format"
        val measurement = SensorMeasurement.parse(rawData, SensorType.TEMPERATURE)
        
        assertNull(measurement)
    }
    
    @Test
    fun `should return null for missing sensor_id`() {
        val rawData = "value=30"
        val measurement = SensorMeasurement.parse(rawData, SensorType.TEMPERATURE)
        
        assertNull(measurement)
    }
    
    @Test
    fun `should return null for missing value`() {
        val rawData = "sensor_id=t1"
        val measurement = SensorMeasurement.parse(rawData, SensorType.TEMPERATURE)
        
        assertNull(measurement)
    }
}

class AlarmTest {
    
    @Test
    fun `should create alarm message with correct format`() {
        val alarm = Alarm(
            sensorId = "t1",
            sensorType = SensorType.TEMPERATURE,
            currentValue = 40.0,
            threshold = 35.0
        )
        
        val message = alarm.getMessage()
        
        assertTrue(message.contains("ALARM TRIGGERED"))
        assertTrue(message.contains("t1"))
        assertTrue(message.contains("TEMPERATURE"))
        assertTrue(message.contains("40.0"))
        assertTrue(message.contains("35.0"))
    }
    
    @Test
    fun `should create humidity alarm message`() {
        val alarm = Alarm(
            sensorId = "h1",
            sensorType = SensorType.HUMIDITY,
            currentValue = 65.0,
            threshold = 50.0
        )
        
        val message = alarm.getMessage()
        
        assertTrue(message.contains("HUMIDITY"))
        assertTrue(message.contains("h1"))
    }
}
