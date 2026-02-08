package com.warehouse.monitoring.warehouseservice.simulator

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

@Component
@EnableScheduling
@ConditionalOnProperty(
    prefix = "warehouse.simulator",
    name = ["enabled"],
    havingValue = "true"
)
class SensorSimulator {
    
    @Value("\${warehouse.sensors.temperature.port}")
    private var temperaturePort: Int = 3344
    
    @Value("\${warehouse.sensors.humidity.port}")
    private var humidityPort: Int = 3355
    
    @Value("\${warehouse.simulator.temperature.sensor-id}")
    private lateinit var temperatureSensorId: String
    
    @Value("\${warehouse.simulator.humidity.sensor-id}")
    private lateinit var humiditySensorId: String
    
    @Value("\${warehouse.simulator.temperature.min-value}")
    private var tempMinValue: Double = 20.0
    
    @Value("\${warehouse.simulator.temperature.max-value}")
    private var tempMaxValue: Double = 45.0
    
    @Value("\${warehouse.simulator.humidity.min-value}")
    private var humidityMinValue: Double = 30.0
    
    @Value("\${warehouse.simulator.humidity.max-value}")
    private var humidityMaxValue: Double = 65.0
    
    private val localhost = InetAddress.getByName("localhost")
    
    @Scheduled(fixedDelayString = "\${warehouse.simulator.interval-ms}")
    fun simulateTemperatureSensor() {
        val value = Random.nextDouble(tempMinValue, tempMaxValue)
        val message = "sensor_id=$temperatureSensorId; value=${"%.2f".format(value)}"
        
        sendUdpMessage(message, temperaturePort)
        logger.info { "🔸 Simulated Temperature: $message" }
    }
    
    @Scheduled(fixedDelayString = "\${warehouse.simulator.interval-ms}", initialDelay = 2000)
    fun simulateHumiditySensor() {
        val value = Random.nextDouble(humidityMinValue, humidityMaxValue)
        val message = "sensor_id=$humiditySensorId; value=${"%.2f".format(value)}"
        
        sendUdpMessage(message, humidityPort)
        logger.info { "🔹 Simulated Humidity: $message" }
    }
    
    private fun sendUdpMessage(message: String, port: Int) {
        try {
            DatagramSocket().use { socket ->
                val buffer = message.toByteArray()
                val packet = DatagramPacket(buffer, buffer.size, localhost, port)
                socket.send(packet)
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to send UDP message to port $port" }
        }
    }
}
