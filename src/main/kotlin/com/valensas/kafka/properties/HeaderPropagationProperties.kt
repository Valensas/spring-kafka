package com.valensas.kafka.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.kafka.propagation")
data class HeaderPropagationProperties(
    val headers: List<String> = emptyList(),
    val contextKey: String = "kafkaPropagatedHeaders"
)
