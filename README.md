# Valensas Spring Kafka Library

This library contains the minimum requirements set by Valensas for kafka libraries that use kafka producer or consumer.

These:
- Produces a specification complying with the asyncapi standard via SpringWolf.
- Adds org.springframework.kafka:spring-kafka dependency.
- Adds kafka consumer deserializers for the input of methods containing the @KafkaListener annotation
- Adds header propagation support between all http and kafka calls.

Supported Versions:
The library is available for the following versions. It may also work for older versions, but it has not been tested.

| Dependency | Condition | Version |
| ------ |-----------|---------|
| Spring Boot | >=        | 3.2.5   |
| Java | >=        | 17      |


## Configuration:
```yaml
springwolf:
    docket:
        base-package: com.valensas
        info:
            title: ${spring.application.name}
            version: 1.0.0
        servers: {}
```

## Header Propagation

To use just add headers that will be propagated to the list. 

```yaml
spring:
  kafka:
    propagation:
      enabled: true
      headers:
        - x
```

To access the headers in the code, you can use the `ThreadLocalHeaderStore.headers` variable.

```kotlin
val headers = ThreadLocalHeaderStore.headers
```

It is imported with `import com.valensas.kafka.config.ThreadLocalHeaderStore` and globally available.



### Caution

When using header propagation be careful about parallel processing. New threads will not have the same thread local variable
and thus `ThreadLocalHeaderStore.headers` should be captured before starting parallel execution.

Example:
```kotlin
val capturedHeaders = ThreadLocalHeaderStore.headers
arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).toList().parallelStream().forEach {
        ThreadLocalHeaderStore.headers = capturedHeaders
        println(ThreadLocalHeaderStore.headers)
}
```

Header propagation is done through a configured RestTemplate, Kafka Consumer/ProducerFactory and a OncePerRequestFilter, also RequestInterceptor if using Feign.
Be careful when creating beans of these types.