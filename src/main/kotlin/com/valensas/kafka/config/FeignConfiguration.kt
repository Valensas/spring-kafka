package com.valensas.kafka.config

import com.valensas.kafka.interceptor.FeignHeaderPropagationInterceptor
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnClass(feign.RequestInterceptor::class)
class FeignConfiguration {
    @Bean
    fun feignHeaderPropagationInterceptor(): FeignHeaderPropagationInterceptor {
        return FeignHeaderPropagationInterceptor()
    }
}
