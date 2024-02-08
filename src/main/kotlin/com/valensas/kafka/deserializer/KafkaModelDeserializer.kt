package com.valensas.kafka.deserializer

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Deserializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class KafkaModelDeserializer(
    private val mapper: ObjectMapper,
    private val mapping: Map<String, Class<*>?>
) : Deserializer<Any> {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun configure(
        configs: MutableMap<String, *>?,
        isKey: Boolean
    ) = Unit

    override fun deserialize(
        topic: String,
        data: ByteArray?
    ): Any? {
        if (data == null) {
            return null
        }
        logger.debug("Kafka message is received by {} topic. Message: {}", topic, String(data))
        return mapping[topic]?.let { mapper.readValue(data, it) }
    }

    override fun close() = Unit
}
