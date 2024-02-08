# Valensas Spring Kafka Library

This library contains the minimum requirements set by Valensas for kafka libraries that use kafka producer or consumer.

These:
- Produces a specification complying with the asyncapi standard via SpringWolf.

Supported Versions:
The library is available for the following versions. It may also work for older versions, but it has not been tested.

| Dependency | Condition | Version |
| ------ | ------ |------ |
| Spring Boot | >= | 3.2.2 |
| Java | = | 21 |


Configuration:
```yaml
springwolf:
    docket:
        base-package: com.valensas
        info:
            title: ${spring.application.name}
            version: 1.0.0
        servers: {}
```