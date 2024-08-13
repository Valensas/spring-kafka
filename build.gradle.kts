import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.github.ben-manes.versions") version "0.51.0"
    id("org.jmailen.kotlinter") version "4.3.0"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    id("net.thebugmc.gradle.sonatype-central-portal-publisher") version "1.2.3"
    id("maven-publish")
}

group = "com.valensas"
version = "0.3.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    mavenLocal()
}

tasks.getByName<Jar>("jar") {
    archiveClassifier.set("")
}

dependencies {

    compileOnly("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.1")
    compileOnly("org.springframework.boot:spring-boot-starter-web")

    // Autoconfiguration
    implementation("org.springframework.boot:spring-boot-autoconfigure")

    // Reflection
    implementation("org.reflections:reflections:0.10.2")

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Kafka
    api("org.springframework.kafka:spring-kafka")

    // Kafka documentation support
    implementation("io.github.springwolf:springwolf-ui:1.1.0")
    implementation("io.github.springwolf:springwolf-asyncapi:1.1.0")
    implementation("io.github.springwolf:springwolf-kafka:1.1.0")
    implementation("org.openfolder:kotlin-asyncapi-spring-web:3.0.3")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.kafka:spring-kafka-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

signing {
    val keyId = System.getenv("SIGNING_KEYID")
    val secretKey = System.getenv("SIGNING_SECRETKEY")
    val passphrase = System.getenv("SIGNING_PASSPHRASE")

    useInMemoryPgpKeys(keyId, secretKey, passphrase)
}

centralPortal {
    username = System.getenv("SONATYPE_USERNAME")
    password = System.getenv("SONATYPE_PASSWORD")

    pom {
        name = "Valensas Kafka"
        description = "This library contains the minimum requirements set by Valensas for kafka libraries that use kafka producer or consumer."
        url = "https://valensas.com/"
        scm {
            url = "https://github.com/Valensas/spring-kafka"
        }

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("0")
                name.set("Valensas")
                email.set("info@valensas.com")
            }
        }
    }
}

