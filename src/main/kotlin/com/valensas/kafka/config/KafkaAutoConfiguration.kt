package com.valensas.kafka.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.valensas.kafka.deserializer.KafkaModelDeserializer
import io.github.springwolf.core.configuration.properties.SpringwolfConfigProperties
import org.apache.kafka.clients.producer.ProducerConfig
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
        interceptors: List<ProducerInterceptorClassHolder>,
        objectMapper: ObjectMapper,
        properties: KafkaProperties
    ): ProducerFactory<String, *> {
        val producerProperties = properties.buildProducerProperties(null)
        producerProperties[ProducerConfig.INTERCEPTOR_CLASSES_CONFIG] =
            interceptors.joinToString { it.producerInterceptorClass.getName() }
        val factory = DefaultKafkaProducerFactory<String, Any>(producerProperties)
        factory.valueSerializer = JsonSerializer(objectMapper)
        return factory
    }

    @Bean
    @ConditionalOnProperty("spring.kafka.consumer.enabled", havingValue = "true")
    fun consumerFactory(
        interceptors: List<ConsumerInterceptorClassHolder>,
        deserializer: KafkaModelDeserializer,
        properties: KafkaProperties,
        applicationContext: ApplicationContext
    ): ConsumerFactory<*, *> {
        val consumerProperties = properties.buildConsumerProperties(null)
        consumerProperties[ProducerConfig.INTERCEPTOR_CLASSES_CONFIG] =
            interceptors.joinToString { it.consumerInterceptorClass.getName() }
        consumerProperties["applicationContext"] = applicationContext
        val factory = DefaultKafkaConsumerFactory<Any, Any>(consumerProperties)
        factory.setValueDeserializer(deserializer)
        return factory
    }

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<*, *>): KafkaTemplate<*, *> {
        return KafkaTemplate(producerFactory)
    }
}
