import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"

    kotlin("plugin.serialization") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    id("org.jetbrains.kotlin.jvm") version "1.9.24"
}
val springBootAdminVersion by extra("3.3.2")

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

tasks {
    getByName<BootJar>("bootJar") {
        archiveFileName.set("Oxidator.jar")
    }

    withType<Test> {
        useJUnitPlatform()
    }
}

dependencies {
    implementation("net.lingala.zip4j:zip4j:2.11.5")
    implementation("club.minnced:jda-ktx:0.11.0-beta.20")
    implementation("software.amazon.awssdk:s3:2.27.17")

    implementation("net.dv8tion:JDA:5.0.0-beta.24") {
        exclude(module = "opus-java")
    }

    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
    implementation("com.github.LeeGodSRC:builtbybit-kotlin-api-wrapper:1.0.3")

    implementation("com.google.guava:guava:33.2.1-jre")
    implementation("commons-io:commons-io:2.16.1")

    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-jersey")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
