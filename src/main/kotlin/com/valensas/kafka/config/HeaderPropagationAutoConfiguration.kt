package com.valensas.kafka.config

import com.valensas.kafka.interceptor.WebHeaderExtractorFilter
import com.valensas.kafka.properties.HeaderPropagationProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestTemplate
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableConfigurationProperties(HeaderPropagationProperties::class)
@ConditionalOnProperty(
    prefix = "spring.kafka.propagation",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true
)
class HeaderPropagationAutoConfiguration : WebMvcConfigurer {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun headerPropagationFilter(headerPropagationProperties: HeaderPropagationProperties): OncePerRequestFilter {
        return WebHeaderExtractorFilter(headerPropagationProperties)
    }

    @Bean
    fun restTemplate(headerPropagationProperties: HeaderPropagationProperties): RestTemplate {
        val restTemplate = RestTemplate()
        restTemplate.interceptors.add(
            ClientHttpRequestInterceptor { request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution ->
                ThreadLocalHeaderStore.headers.forEach {
                    request.headers[it.key] = listOf(it.value)
                }
                ThreadLocalHeaderStore.clear()
                execution.execute(request, body)
            }
        )
        return restTemplate
    }
}
