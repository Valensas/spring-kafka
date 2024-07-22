package com.valensas.kafka.interceptor


import com.valensas.kafka.config.ThreadLocalHeaderStore
import com.valensas.kafka.properties.HeaderPropagationProperties
import org.apache.kafka.clients.consumer.ConsumerInterceptor
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.header.Header
import java.util.function.Consumer

class KafkaHeaderPropagationConsumerInterceptor<K, V> : ConsumerInterceptor<K, V> {

    private lateinit var headerPropagationProperties: HeaderPropagationProperties

    override fun onConsume(records: ConsumerRecords<K, V>): ConsumerRecords<K, V> {
        records.forEach(Consumer { record: ConsumerRecord<K, V> ->
            val headers = record.headers()
            val headerMap: MutableMap<String, String> =
                ThreadLocalHeaderStore.headers.toMutableMap()
            headers.forEach(Consumer { header: Header ->
                if (headerPropagationProperties.headers.contains(header.key()))
                    headerMap[header.key()] = String(header.value())
            })
            ThreadLocalHeaderStore.headers = headerMap
        })
        return records
    }

    override fun close() {
    }

    override fun onCommit(p0: MutableMap<TopicPartition, OffsetAndMetadata>?) {
    }

    override fun configure(configs: Map<String, *>) {
        headerPropagationProperties = configs["headerPropagationProperties"] as HeaderPropagationProperties
    }
}
