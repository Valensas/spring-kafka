import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.jmailen.kotlinter") version "4.2.0"
    id("maven-publish")
    id("java-library")
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
}

group = "com.valensas"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}


dependencies {
    // Kafka documentation support
    implementation("io.github.springwolf:springwolf-ui:0.18.0")
    implementation("io.github.springwolf:springwolf-asyncapi:0.18.0")
    implementation("io.github.springwolf:springwolf-kafka:0.18.0")
    implementation("org.openfolder:kotlin-asyncapi-spring-web:3.0.3")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-web")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
