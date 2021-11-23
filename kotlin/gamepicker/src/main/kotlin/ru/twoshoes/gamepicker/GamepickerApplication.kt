package ru.twoshoes.gamepicker

import io.minio.BucketExistsArgs
import io.minio.MinioClient
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.twoshoes.gamepicker.configuration.property.MinioProperties

@SpringBootApplication
@ConfigurationPropertiesScan
class GamepickerApplication

fun main(args: Array<String>) {
    runApplication<GamepickerApplication>(*args)
}

@Component
class Foo(
    private val minioClient: MinioClient,
    private val minioProperties: MinioProperties
) {

    @EventListener(ApplicationReadyEvent::class)
    fun test() {
        val bucketExists = runCatching {
            minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.bucket).build())
        }.getOrElse { error ->
            println("Can not sure bucker exists: ${error.message}")
        }

        println("bucket exists = $bucketExists")
    }
}
