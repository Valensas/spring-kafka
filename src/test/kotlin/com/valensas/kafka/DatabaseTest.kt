package com.valensas.kafka

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class DatabaseTest(
    @Autowired
    private val restTemplate: TestRestTemplate
) {
    @Test
    fun `can open asyncapi documentation`() {
        val result = restTemplate.getForEntity("/springwolf/asyncapi-ui.html", String::class.java)
        assertTrue(result.statusCode.is2xxSuccessful)
    }
}
