package io.liftgate.oxidator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@EnableAsync
@EnableScheduling
@SpringBootApplication(exclude = [ErrorMvcAutoConfiguration::class])
class OxidatorApplication

fun main(args: Array<String>)
{
    runApplication<OxidatorApplication>(*args)
}

