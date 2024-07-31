package com.valensas.kafka.config

import com.valensas.kafka.interceptor.FeignHeaderPropagationInterceptor
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnClass(feign.RequestInterceptor::class)
@ConditionalOnProperty(
    prefix = "spring.kafka.propagation",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true
)
class FeignConfiguration {
    @Bean
    fun feignHeaderPropagationInterceptor(): FeignHeaderPropagationInterceptor {
        return FeignHeaderPropagationInterceptor()
    }
}
