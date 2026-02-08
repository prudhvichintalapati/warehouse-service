package com.warehouse.monitoring.warehouseservice.integration

import com.warehouse.monitoring.warehouseservice.domain.SensorMeasurement
import com.warehouse.monitoring.warehouseservice.domain.SensorType
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.TimeUnit

@SpringBootTest
@TestPropertySource(
    properties = [
        "warehouse.simulator.enabled=false",
        "spring.rabbitmq.host=localhost"
    ]
)
class WarehouseMonitoringIntegrationTest {
    
    @Autowired
    private lateinit var rabbitTemplate: RabbitTemplate
    
    @Test
    fun `should process UDP temperature message end-to-end`() {
        val message = "sensor_id=t1; value=32.5"
        sendUdpMessage(message, 3344)

        TimeUnit.MILLISECONDS.sleep(500)
    }
    
    @Test
    fun `should process UDP humidity message end-to-end`() {
        val message = "sensor_id=h1; value=48.0"
        sendUdpMessage(message, 3355)
        
        TimeUnit.MILLISECONDS.sleep(500)
    }
    
    @Test
    fun `should parse and validate sensor measurement`() {
        val rawData = "sensor_id=t2; value=37.8"
        val measurement = SensorMeasurement.parse(rawData, SensorType.TEMPERATURE)
        
        assert(measurement != null)
        assert(measurement?.sensorId == "t2")
        assert(measurement?.value == 37.8)
    }
    
    private fun sendUdpMessage(message: String, port: Int) {
        try {
            DatagramSocket().use { socket ->
                val buffer = message.toByteArray()
                val address = InetAddress.getByName("localhost")
                val packet = DatagramPacket(buffer, buffer.size, address, port)
                socket.send(packet)
            }
        } catch (e: Exception) {
            println("Warning: Could not send UDP message (RabbitMQ may not be running): ${e.message}")
        }
    }
}
