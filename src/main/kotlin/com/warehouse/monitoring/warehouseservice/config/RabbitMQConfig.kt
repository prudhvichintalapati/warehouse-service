package com.warehouse.monitoring.warehouseservice.config

import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {
    
    @Value("\${messaging.exchange.sensor-data}")
    private lateinit var exchangeName: String
    
    @Value("\${messaging.queue.temperature}")
    private lateinit var temperatureQueueName: String
    
    @Value("\${messaging.queue.humidity}")
    private lateinit var humidityQueueName: String
    
    @Value("\${messaging.routing-key.temperature}")
    private lateinit var temperatureRoutingKey: String
    
    @Value("\${messaging.routing-key.humidity}")
    private lateinit var humidityRoutingKey: String
    
    @Bean
    fun exchange(): TopicExchange {
        return TopicExchange(exchangeName)
    }
    
    @Bean
    fun temperatureQueue(): Queue {
        return Queue(temperatureQueueName, true)
    }
    
    @Bean
    fun humidityQueue(): Queue {
        return Queue(humidityQueueName, true)
    }
    
    @Bean
    fun temperatureBinding(temperatureQueue: Queue, exchange: TopicExchange): Binding {
        return BindingBuilder
            .bind(temperatureQueue)
            .to(exchange)
            .with(temperatureRoutingKey)
    }
    
    @Bean
    fun humidityBinding(humidityQueue: Queue, exchange: TopicExchange): Binding {
        return BindingBuilder
            .bind(humidityQueue)
            .to(exchange)
            .with(humidityRoutingKey)
    }
    
    @Bean
    fun messageConverter(): MessageConverter {
        return Jackson2JsonMessageConverter()
    }
    
    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        val template = RabbitTemplate(connectionFactory)
        template.messageConverter = messageConverter()
        return template
    }
}
