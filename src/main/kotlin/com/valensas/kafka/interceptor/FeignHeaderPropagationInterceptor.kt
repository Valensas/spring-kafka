package com.valensas.kafka.interceptor

import com.valensas.kafka.config.ThreadLocalHeaderStore
import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.stereotype.Component

@Component
@ConditionalOnClass(feign.RequestInterceptor::class)
class FeignHeaderPropagationInterceptor : RequestInterceptor {
    override fun apply(template: RequestTemplate) {
        ThreadLocalHeaderStore.headers.forEach { (key, value) ->
            template.header(key, value)
        }
    }
}
