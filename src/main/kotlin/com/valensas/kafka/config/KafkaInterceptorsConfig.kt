package com.valensas.kafka.config

import org.apache.kafka.clients.consumer.ConsumerInterceptor
import org.apache.kafka.clients.producer.ProducerInterceptor

data class ProducerInterceptorClassHolder(
    val producerInterceptorClass: Class<out ProducerInterceptor<*, *>>
)

data class ConsumerInterceptorClassHolder(
    val consumerInterceptorClass: Class<out ConsumerInterceptor<*, *>>
)