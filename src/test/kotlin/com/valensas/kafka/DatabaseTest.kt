package com.valensas.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.context.ActiveProfiles
import java.time.Duration
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EmbeddedKafka
class DatabaseTest(
    @Autowired
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    @Autowired
    private val restTemplate: TestRestTemplate,
    @Autowired
    private val embeddedKafkaBroker: EmbeddedKafkaBroker
) {
    @Test
    fun `can open asyncapi documentation`() {
        val result = restTemplate.getForEntity("/springwolf/asyncapi-ui.html", String::class.java)
        assertTrue(result.statusCode.is2xxSuccessful)
    }

    @Test
    fun `can publish kafka message`() {
        val topic = "test"
        val consumerProps =
            KafkaTestUtils.consumerProps(
                "test-group",
                "true",
                embeddedKafkaBroker
            )
        consumerProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        val consumer = DefaultKafkaConsumerFactory<String, Any?>(consumerProps).createConsumer()
        consumer.subscribe(listOf(topic))

        val publishedMessage =
            KafkaTestMessage(
                message = UUID.randomUUID().toString()
            )
        val producerRecord = ProducerRecord<String, Any>(topic, publishedMessage)
        kafkaTemplate.send(producerRecord)

        val messages = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10))
        assertEquals(1, messages.count())
        val consumedMessage = ObjectMapper().readValue(messages.first().value().toString(), KafkaTestMessage::class.java)
        assertEquals(publishedMessage, consumedMessage)
    }

    data class KafkaTestMessage(
        var message: String
    ) {
        constructor() : this("")
    }
}
