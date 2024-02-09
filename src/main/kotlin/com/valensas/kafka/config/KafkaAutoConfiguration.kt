package com.valensas.kafka.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.valensas.kafka.deserializer.KafkaModelDeserializer
import io.github.stavshamir.springwolf.configuration.AsyncApiDocket
import io.github.stavshamir.springwolf.configuration.properties.SpringwolfConfigProperties
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.springframework.beans.factory.support.AbstractBeanFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
class KafkaAutoConfiguration {
    @Bean
    @ConditionalOnProperty("spring.kafka.consumer.enabled", havingValue = "true")
    fun kafkaModelDeserializer(
        objectMapper: ObjectMapper,
        applicationContext: ApplicationContext,
        properties: SpringwolfConfigProperties
    ): KafkaModelDeserializer {
        val reflections = Reflections(properties.docket?.basePackage, Scanners.MethodsAnnotated)
        val mapping =
            reflections
                .getMethodsAnnotatedWith(KafkaListener::class.java)
                .map {
                    val topics =
                        it.getAnnotation(KafkaListener::class.java).topics.mapNotNull {
                            (applicationContext.autowireCapableBeanFactory as AbstractBeanFactory).resolveEmbeddedValue(it)
                        }
                    topics.map { topic -> topic to it.parameterTypes.firstOrNull() }
                }.flatten().toMap()
        return KafkaModelDeserializer(objectMapper, mapping)
    }

    @Bean
    fun producerFactory(
        objectMapper: ObjectMapper,
        properties: KafkaProperties
    ): ProducerFactory<String, *> {
        val factory = DefaultKafkaProducerFactory<String, Any>(properties.buildProducerProperties(null))
        factory.setValueSerializer(JsonSerializer(objectMapper))
        return factory
    }

    @Bean
    @ConditionalOnProperty("spring.kafka.consumer.enabled", havingValue = "true")
    fun consumerFactory(
        deserializer: KafkaModelDeserializer,
        properties: KafkaProperties
    ): ConsumerFactory<*, *> {
        val factory = DefaultKafkaConsumerFactory<Any, Any>(properties.buildConsumerProperties(null))
        factory.setValueDeserializer(deserializer)
        return factory
    }

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<*, *>): KafkaTemplate<*, *> {
        return KafkaTemplate(producerFactory)
    }
}
